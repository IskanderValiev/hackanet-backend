package com.hackanet.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum LocalMessageText {
    EN(
            "The user has accepted the job offer. " +
                    "The chat was created for convenient communication " +
                    "to each other for the user and the company."
    ),
    RU (
            "Пользователь принял предложение о работе. " +
                    "Чат был создан для удобного общения между пользователем и компанией."
    ),
    DE (
            "Der Benutzer hat das Stellenangebot angenommen. " +
            "Der Chat wurde für die bequeme Kommunikation zwischen Benutzer " +
            "und Unternehmen erstellt."
    ),
    FR (
            "L'utilisateur a accepté l'offre d'emploi. " +
                    "Le chat a été créé pour faciliter la communication " +
                    "entre l'utilisateur et l'entreprise."
    );

    private String acceptedJobOffer;
}
