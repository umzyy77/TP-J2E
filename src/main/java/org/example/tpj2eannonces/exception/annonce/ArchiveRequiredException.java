package org.example.tpj2eannonces.exception.annonce;

import org.example.tpj2eannonces.exception.ConflictException;

public class ArchiveRequiredException extends ConflictException {

    public ArchiveRequiredException() {
        super("Une annonce doit etre archivee avant d'etre supprimee");
    }
}
