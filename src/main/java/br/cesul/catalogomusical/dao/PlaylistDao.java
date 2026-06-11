package br.cesul.catalogomusical.dao;

// DAO da coleção 'playlists'. Versão piloto, tudo implementado.

// O ponto novo aqui em relação ao quiz battle é que a Playlist tem
// um ARRAY (musicaIds) e a gente precisa adicionar/remover
// elementos desse array via mongo. Pra isso temos dois operadores:
//   addToSet  -> adiciona no array sem duplicar
//   pull      -> remove TODAS as ocorrências do valor

import br.cesul.catalogomusical.model.Playlist;
import br.cesul.catalogomusical.util.MongoConfig;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.set;

public class PlaylistDao {

    private final MongoCollection<Document> col = MongoConfig.playlists();

    // Conversão Document -> Playlist.
    // Cuidado com null: playlist recém criada pode não ter o array
    // musicaIds salvo ainda. A gente troca por uma lista vazia.
    private Playlist toPlaylist(Document d){
        List<String> ids = d.getList("musicaIds", String.class);
        if (ids == null) ids = new ArrayList<>();
        return new Playlist(
                d.getObjectId("_id").toHexString(),
                d.getString("nome"),
                ids
        );
    }

    // Lista todas as playlists ordenadas por nome.
    public List<Playlist> findAll(){
        List<Playlist> list = new ArrayList<>();
        for (Document d : col.find().sort(Sorts.ascending("nome"))){
            list.add(toPlaylist(d));
        }
        return list;
    }

    // Cria uma playlist vazia (com o array musicaIds vazio).
    public void insert(String nome){
        if (nome == null || nome.isBlank()) return;
        col.insertOne(new Document()
                .append("nome", nome)
                .append("musicaIds", new ArrayList<String>()));
    }

    // Adiciona uma música no array musicaIds da playlist.
    // addToSet: se já existe não duplica, se não existe adiciona.
    // É a operação certa pra "música x playlist": não faz sentido
    // ter a mesma música duas vezes no mesmo conjunto.
    public void adicionarMusica(String playlistId, String musicaId){
        col.updateOne(
                eq("_id", new ObjectId(playlistId)),
                addToSet("musicaIds", musicaId)
        );
    }

    // Tira uma música da playlist.
    // pull: encontra o valor e remove do array.
    public void removerMusica(String playlistId, String musicaId){
        col.updateOne(
                eq("_id", new ObjectId(playlistId)),
                pull("musicaIds", musicaId)
        );
    }

    // Renomeia. Aqui o que muda é UM campo só -> usamos set.
    public void renomear(String playlistId, String novoNome){
        if (novoNome == null || novoNome.isBlank()) return;
        col.updateOne(
                eq("_id", new ObjectId(playlistId)),
                set("nome", novoNome)
        );
    }

    // Apaga a playlist. Mesmo esquema do delete em MusicaDao.
    public void delete(String id){
        col.deleteOne(eq("_id", new ObjectId(id)));
    }
}
