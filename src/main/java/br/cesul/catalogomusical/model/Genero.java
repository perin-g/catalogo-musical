package br.cesul.catalogomusical.model;

// Gênero musical de uma música.
// Mesmo motivo de sempre usar enum: a lista é fixa,
// o compilador me protege contra digitar "Rock" e "ROCK"
// como se fossem coisas diferentes, e eu ainda consigo
// dar um rótulo bonitinho pra mostrar na tela.
public enum Genero {
    ROCK,
    POP,
    MPB,
    ELETRONICO,
    CLASSICO,
    JAZZ,
    RAP;

    // Texto que vai aparecer pro usuário.
    // O nome da constante (ROCK) é o nome no código,
    // o rótulo é o nome amigável (Rock).
    public String rotulo(){
        return switch (this) {
            case ROCK       -> "Rock";
            case POP        -> "Pop";
            case MPB        -> "MPB";
            case ELETRONICO -> "Eletrônico";
            case CLASSICO   -> "Clássica";
            case JAZZ       -> "Jazz";
            case RAP        -> "Rap";
        };
    }
}
