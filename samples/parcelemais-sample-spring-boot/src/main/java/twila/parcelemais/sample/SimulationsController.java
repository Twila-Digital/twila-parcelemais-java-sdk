package twila.parcelemais.sample;

import twila.parcelemais.ParceleMaisClient;
import twila.parcelemais.simulations.model.InstallmentSimulation;
import twila.parcelemais.simulations.model.SimulateInstallmentsRequest;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulationsController {

    private final ParceleMaisClient client;

    public SimulationsController(ParceleMaisClient client) {
        this.client = client;
    }

    @GetMapping("/simulations/installments")
    public List<InstallmentSimulation> simulateInstallments(@RequestParam BigDecimal requestedAmount) {
        return client.simulations().simulateInstallments(
                SimulateInstallmentsRequest.builder().requestedAmount(requestedAmount).build());
    }
}
