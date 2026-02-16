package org.example.tpj2eannonces.exception.annonce;

import org.example.tpj2eannonces.exception.ConflictException;

public class AnnonceImmutableException extends ConflictException {

    public AnnonceImmutableException() {
        super("Une annonce publiee ne peut plus etre modifiee");
    }
}
