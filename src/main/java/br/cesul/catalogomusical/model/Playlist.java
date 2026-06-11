package br.cesul.catalogomusical.model;

import java.util.List;

// Representa uma PLAYLIST: um nome + uma lista de IDs de músicas.

// Pq guardamos só os IDs em vez de salvar os Musica inteiros?
// - Se a música for editada (corrigir um nome de artista, por exemplo)
//   não preciso achar todas as playlists pra atualizar — elas continuam
//   apontando pro mesmo id e na próxima vez já vem a versão nova.
// - É a mesma ideia de FK do banco relacional: aponto pra entidade,
//   não copio ela.

// Quando precisar mostrar as músicas de uma playlist na tela,
// a gente pega os IDs daqui e busca no MusicaDao (findByIds).
// Isso é o que vamos chamar de "join manual".

public record Playlist(
        String id,
        String nome,
        List<String> musicaIds
) {
    // Atalho útil. Em vez de chamar pl.musicaIds().size() na UI,
    // chamo pl.quantidade(). E ainda blindo contra null.
    public int quantidade(){
        return musicaIds == null ? 0 : musicaIds.size();
    }
}
