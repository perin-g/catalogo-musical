package br.cesul.catalogomusical.model;

// Representa UMA música do catálogo.
// Continua sendo um record igual o Player do quiz: imutável,
// sem boilerplate, getters já vêm prontos como m.titulo(), m.artista()...

// Quando precisar atualizar algo (editar), a gente
// não muda o objeto — chama o dao.update e ele grava
// o novo estado no banco. Aí no próximo findAll já vem
// um Musica novo.

public record Musica(
        String id,              // o _id do mongo, em hex
        String titulo,
        String artista,
        String album,
        int duracaoSegundos,    // guardamos em segundos pra calcular fácil
        Genero genero
) {
    // Records podem ter métodos normais. Aqui transformo a
    // duração de segundos pra "mm:ss" porque é assim que
    // a gente quer mostrar na tela (367 vira "6:07").
    public String duracaoFormatada(){
        int min = duracaoSegundos / 60;
        int seg = duracaoSegundos % 60;
        // o %02d garante "6:07" e não "6:7"
        return String.format("%d:%02d", min, seg);
    }
}
