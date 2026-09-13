package twila.parcelemais.errors;

import twila.parcelemais.internal.http.ApiResponse;
import twila.parcelemais.serialization.ParceleMaisObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class ParceleMaisExceptionFactory {

    private ParceleMaisExceptionFactory() {
    }

    public static ParceleMaisException fromResponse(ApiResponse response) {
        ProblemDetailsModel problemDetails = readProblemDetails(response.body());
        String message = selectMessage(problemDetails, response.statusCode());

        int statusCode = response.statusCode();

        if (statusCode == 401)
            return new ParceleMaisAuthenticationException(message);

        if (statusCode == 400 && problemDetails.getErrors() != null && !problemDetails.getErrors().isEmpty())
            return new ParceleMaisValidationException(message, problemDetails);

        if (statusCode == 429)
            return new ParceleMaisRateLimitException(message, problemDetails, retryAfter(response));

        return new ParceleMaisApiException(message, statusCode, problemDetails);
    }

    private static String selectMessage(ProblemDetailsModel problemDetails, int statusCode) {
        if (problemDetails.getDetail() != null)
            return problemDetails.getDetail();

        if (problemDetails.getTitle() != null)
            return problemDetails.getTitle();

        return "A API do Parcele+ retornou " + statusCode + ".";
    }

    private static ProblemDetailsModel readProblemDetails(String body) {
        if (body == null || body.trim().isEmpty())
            return ProblemDetailsModel.empty();

        try {
            ProblemDetailsModel problemDetails = ParceleMaisObjectMapper.DEFAULT.readValue(body, ProblemDetailsModel.class);
            return problemDetails != null ? problemDetails : ProblemDetailsModel.empty();
        } catch (Exception ex) {
            return ProblemDetailsModel.empty();
        }
    }

    private static Duration retryAfter(ApiResponse response) {
        String retryAfter = response.headers().get("Retry-After");
        return retryAfter != null ? parseRetryAfter(retryAfter) : null;
    }

    private static Duration parseRetryAfter(String value) {
        try {
            return Duration.ofSeconds(Long.parseLong(value.trim()));
        } catch (NumberFormatException ex) {
            try {
                Instant target = ZonedDateTime.parse(value.trim(), DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
                Duration delta = Duration.between(Instant.now(), target);
                return delta.isNegative() ? Duration.ZERO : delta;
            } catch (Exception parseEx) {
                return null;
            }
        }
    }
}
