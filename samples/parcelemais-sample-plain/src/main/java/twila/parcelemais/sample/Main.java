package twila.parcelemais.sample;

import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.config.ParceleMaisEnvironment;
import twila.parcelemais.errors.ParceleMaisApiException;
import twila.parcelemais.simulations.model.InstallmentSimulation;
import twila.parcelemais.simulations.model.SimulateInstallmentsRequest;
import java.math.BigDecimal;
import java.util.List;

public final class Main {

    public static void main(String[] args) {
        String clientId = System.getenv("PARCELEMAIS_CLIENT_ID");
        String clientSecret = System.getenv("PARCELEMAIS_CLIENT_SECRET");

        if (clientId == null || clientSecret == null) {
            System.err.println("Defina PARCELEMAIS_CLIENT_ID e PARCELEMAIS_CLIENT_SECRET no ambiente antes de rodar este sample.");
            System.exit(1);
        }

        try (ParceleMaisClient client = ParceleMaisClient.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .environment(ParceleMaisEnvironment.STAGING)
                .build()) {

            List<InstallmentSimulation> parcelas = client.simulations().simulateInstallments(
                    SimulateInstallmentsRequest.builder()
                            .requestedAmount(new BigDecimal("1500.00"))
                            .build());

            for (InstallmentSimulation parcela : parcelas)
                System.out.printf("%dx de %s (total %s)%n", parcela.getTerm(), parcela.getInstallmentAmount(), parcela.getTotalAmount());

        } catch (ParceleMaisApiException ex) {
            System.err.printf("%d %s: %s%n", ex.getStatusCode(), ex.getErrorCode(), ex.getMessage());
        }
    }
}
