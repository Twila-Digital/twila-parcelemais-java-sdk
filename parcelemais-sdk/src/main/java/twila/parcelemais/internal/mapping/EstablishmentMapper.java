package twila.parcelemais.internal.mapping;

import twila.parcelemais.internal.generated.establishment.CreateEstablishmentRequestWire;
import twila.parcelemais.internal.generated.establishment.EstablishmentAddressWire;
import twila.parcelemais.internal.generated.establishment.EstablishmentBankAccountWire;
import twila.parcelemais.internal.generated.establishment.EstablishmentWire;
import twila.parcelemais.internal.generated.establishment.EstablishmentOwnerWire;
import twila.parcelemais.internal.generated.establishment.UpdateEstablishmentRequestWire;
import twila.parcelemais.establishments.model.BankAccountType;
import twila.parcelemais.establishments.model.CreateEstablishmentRequest;
import twila.parcelemais.establishments.model.DisbursementModel;
import twila.parcelemais.establishments.model.Establishment;
import twila.parcelemais.establishments.model.EstablishmentAddress;
import twila.parcelemais.establishments.model.EstablishmentBankAccount;
import twila.parcelemais.establishments.model.EstablishmentOwner;
import twila.parcelemais.establishments.model.UpdateEstablishmentRequest;

public final class EstablishmentMapper {

    private EstablishmentMapper() {
    }

    public static CreateEstablishmentRequestWire toWire(CreateEstablishmentRequest request) {
        return new CreateEstablishmentRequestWire(
                request.getDocument(),
                request.getLegalName(),
                request.getTradeName(),
                request.getDisbursementModel().wireValue(),
                toWire(request.getOwner()),
                toWire(request.getBankAccount()),
                toWire(request.getAddress()));
    }

    public static UpdateEstablishmentRequestWire toWire(UpdateEstablishmentRequest request) {
        return new UpdateEstablishmentRequestWire(
                request.getTradeName(),
                request.getDisbursementModel() == null ? null : request.getDisbursementModel().wireValue(),
                toWire(request.getAddress()));
    }

    public static EstablishmentBankAccountWire toBankAccountWire(EstablishmentBankAccount bankAccount) {
        return toWire(bankAccount);
    }

    public static Establishment toPublic(EstablishmentWire wire) {
        return Establishment.builder()
                .establishmentId(wire.estabelecimentoId)
                .document(wire.documento)
                .legalName(wire.razaoSocial)
                .tradeName(wire.nomeFantasia)
                .isActive(wire.ativa)
                .owner(toPublic(wire.responsavel))
                .disbursementModel(wire.modeloDesembolso == null
                        ? null
                        : DisbursementModel.fromWireValue(wire.modeloDesembolso))
                .bankAccount(wire.contaBancaria == null ? null : toPublic(wire.contaBancaria))
                .address(wire.endereco == null ? null : toPublic(wire.endereco))
                .build();
    }

    private static EstablishmentOwner toPublic(EstablishmentOwnerWire wire) {
        return wire == null
                ? null
                : EstablishmentOwner.builder()
                        .name(wire.nome)
                        .email(wire.email)
                        .phone(wire.celular)
                        .build();
    }

    private static EstablishmentBankAccount toPublic(EstablishmentBankAccountWire wire) {
        return EstablishmentBankAccount.builder()
                .bankNumber(wire.banco)
                .agencyNumber(wire.agencia)
                .agencyDigit(wire.digitoAgencia)
                .accountNumber(wire.conta)
                .accountDigit(wire.digitoConta)
                .accountType(wire.tipoConta == null ? null : BankAccountType.fromWireValue(wire.tipoConta))
                .holderName(wire.nomeTitular)
                .holderDocument(wire.documentoTitular)
                .build();
    }

    private static EstablishmentAddress toPublic(EstablishmentAddressWire wire) {
        return EstablishmentAddress.builder()
                .street(wire.rua)
                .number(wire.numero)
                .complement(wire.complemento)
                .district(wire.bairro)
                .city(wire.cidade)
                .state(wire.estado)
                .zipCode(wire.cep)
                .country(wire.pais)
                .build();
    }

    private static EstablishmentOwnerWire toWire(EstablishmentOwner owner) {
        return owner == null ? null : new EstablishmentOwnerWire(owner.getName(), owner.getEmail(), owner.getPhone());
    }

    private static EstablishmentBankAccountWire toWire(EstablishmentBankAccount bankAccount) {
        if (bankAccount == null) {
            return null;
        }

        return new EstablishmentBankAccountWire(
                bankAccount.getBankNumber(),
                bankAccount.getAgencyNumber(),
                bankAccount.getAgencyDigit() == null ? "" : bankAccount.getAgencyDigit(),
                bankAccount.getAccountNumber(),
                bankAccount.getAccountDigit(),
                bankAccount.getAccountType() == null ? 0 : bankAccount.getAccountType().wireValue(),
                bankAccount.getHolderName(),
                bankAccount.getHolderDocument());
    }

    private static EstablishmentAddressWire toWire(EstablishmentAddress address) {
        if (address == null) {
            return null;
        }

        return new EstablishmentAddressWire(
                address.getStreet(),
                address.getNumber(),
                address.getComplement(),
                address.getDistrict(),
                address.getCity(),
                address.getState(),
                address.getZipCode(),
                address.getCountry());
    }
}
