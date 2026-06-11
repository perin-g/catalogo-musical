package br.cesul.catalogomusical.dao;

// DAO da coleção 'playlists'.
// Mesma ideia dos outros DAOs: a UI nunca toca no mongo,
// passa por aqui.

// Esta classe vc recebe vazia (só com o toPlaylist pronto).
// Todos os métodos são // TODO.

// Novidade nessa aba: nossa playlist guarda um ARRAY de IDs de música.
// E aí, pra adicionar/remover de um array no mongo, a gente usa
// dois operadores novos:
//   addToSet  -> adiciona no array sem duplicar (se ja existe, ignora)
//   pull      -> remove TODAS as ocorrências de um valor do array

import br.cesul.catalogomusical.model.Playlist;
import br.cesul.catalogomusical.util.MongoConfig;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

// importando individualmente, igual o quiz battle
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.pull;
import static com.mongodb.client.model.Updates.set;

public class PlaylistDao {

    private final MongoCollection<Document> col = MongoConfig.playlists();

    // Conversão Document -> Playlist.
    // Aqui tem uma sutileza: o array 'musicaIds' pode não existir
    // (se a playlist foi recém criada). Por isso o tratamento de null.
    private Playlist toPlaylist(Document d){
        List<String> ids = d.getList("musicaIds", String.class);
        if (ids == null) ids = new ArrayList<>();
        return new Playlist(
                d.getObjectId("_id").toHexString(),
                d.getString("nome"),
                ids
        );
    }

    // TODO 1 - listar TODAS as playlists ordenadas pelo nome.
    // Mesma estrutura do findAll do MusicaDao, mas chamando toPlaylist
    // e ordenando por "nome" em vez de "titulo".
    public List<Playlist> findAll(){
        return new ArrayList<>();
    }

    // TODO 2 - criar uma playlist vazia.
    // Igual ao insert do PlayerDao do quiz, mas com 2 campos:
    //   nome       -> o nome que o usuário digitou
    //   musicaIds  -> uma lista de String VAZIA (new ArrayList<String>())
    // Nao esqueça de validar se o nome veio em branco antes de gravar.
    public void insert(String nome){
        // TODO
    }

    // TODO 3 - adicionar uma música na playlist.
    // Diferente do registrarPartida do quiz (que somava pontos),
    // aqui a gente vai mexer num ARRAY do documento.
    //
    // Use col.updateOne passando:
    //   - filtro:     eq("_id", new ObjectId(playlistId))
    //   - alteração:  addToSet("musicaIds", musicaId)
    //
    // O addToSet faz o que o nome diz: adiciona no conjunto. Se a
    // música já estiver na playlist, ele ignora (a gente não quer a
    // mesma música duas vezes).
    public void adicionarMusica(String playlistId, String musicaId){
        // TODO
    }

    // TODO 4 - tirar uma música da playlist.
    // Mesma chamada de cima, trocando addToSet por pull("musicaIds", musicaId).
    // O pull procura o valor no array e remove.
    public void removerMusica(String playlistId, String musicaId){
        // TODO
    }

    // TODO 5 - renomear a playlist.
    // updateOne com filtro no _id e usando set("nome", novoNome).
    // O set é o operador que troca o valor de UM campo (sem mexer no resto).
    // Valida se o nome veio vazio.
    public void renomear(String playlistId, String novoNome){
        // TODO
    }

    // TODO 6 - apagar a playlist.
    // Mesmo modelo do delete do MusicaDao: col.deleteOne com eq no _id.
    public void delete(String id){
        // TODO
    }
}
