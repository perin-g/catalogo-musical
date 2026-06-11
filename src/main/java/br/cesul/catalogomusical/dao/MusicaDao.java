package br.cesul.catalogomusical.dao;

// DAO da coleção 'musicas'.
// Mesmo papel do PlayerDao do quiz: converter Document <-> Musica
// e expor os métodos que a UI pode chamar.

// Você recebe esta classe com PARTE pronta (findAll, findByIds, insert
// e somaDuracao). Os métodos marcados com // TODO ficam pra você.

import br.cesul.catalogomusical.model.Genero;
import br.cesul.catalogomusical.model.Musica;
import br.cesul.catalogomusical.util.MongoConfig;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Mesma ideia do quiz battle: importar só o eq e o in que vão usar
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

public class MusicaDao {

    private final MongoCollection<Document> col = MongoConfig.musicas();

    // Conversão Document -> Musica.
    // Igualzinho ao toPlayer do quiz: pego cada campo do doc e
    // monto o record.
    private Musica toMusica(Document d){
        return new Musica(
                d.getObjectId("_id").toHexString(),
                d.getString("titulo"),
                d.getString("artista"),
                d.getString("album"),
                d.getInteger("duracaoSegundos", 0),
                Genero.valueOf(d.getString("genero"))
        );
    }

    // Lista todas as músicas ordenadas pelo título (A -> Z).
    public List<Musica> findAll(){
        List<Musica> list = new ArrayList<>();
        for (Document d : col.find().sort(Sorts.ascending("titulo"))){
            list.add(toMusica(d));
        }
        return list;
    }

    // Insert básico. Validamos o título antes de gravar pra não
    // sujar o banco com música sem nome.
    public void insert(String titulo, String artista, String album,
                       int duracaoSegundos, Genero genero){
        if (titulo == null || titulo.isBlank()) return;
        col.insertOne(new Document()
                .append("titulo", titulo)
                .append("artista", artista == null ? "" : artista)
                .append("album",   album   == null ? "" : album)
                .append("duracaoSegundos", Math.max(0, duracaoSegundos))
                .append("genero",  genero.name()));   // enum guardado como String
    }

    // Busca várias músicas pelo id de uma vez.
    // Usado pela aba de playlist: tenho uma lista de IDs (musicaIds)
    // e quero trazer todos os Musica correspondentes em uma ida ao banco.
    // Filters.in("_id", lista) é o "where _id IN (...)" do mongo.
    public List<Musica> findByIds(List<String> ids){
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        // converte as strings em ObjectId (o tipo real do _id no mongo)
        List<ObjectId> objIds = new ArrayList<>();
        for (String s : ids) objIds.add(new ObjectId(s));

        List<Musica> list = new ArrayList<>();
        for (Document d : col.find(in("_id", objIds))){
            list.add(toMusica(d));
        }
        return list;
    }

    // TODO 1 - filtrar por gênero
    // Praticamente igual ao findByCategoria do QuestionDao do quiz.
    // 1 - criar lista vazia
    // 2 - iterar col.find(eq("genero", genero.name()))
    // 3 - dentro do for, adicionar com toMusica(d)
    // 4 - retornar a lista
    public List<Musica> findByGenero(Genero genero){
        return new ArrayList<>();
    }

    // TODO 2 - atualizar uma música existente
    // A gente nunca fez UPDATE de vários campos juntos, mas o esquema
    // é o mesmo do registrarPartida do PlayerDao: chamar col.updateOne
    // passando o filtro e o que vai mudar.
    //
    // O filtro é eq("_id", new ObjectId(id))   (igual ao quiz).
    //
    // Para alterar vários campos de uma vez, monte um Document de "$set"
    // com TODOS os campos que vão mudar, assim:
    //
    //     new Document("$set", new Document()
    //         .append("titulo", titulo)
    //         .append("artista", artista)
    //         ... )
    //
    // O $set diz pro mongo "substitui esses campos pelos valores que vou
    // mandar agora". Os campos que voce não citar continuam como estavam.
    public void update(String id, String titulo, String artista, String album,
                       int duracaoSegundos, Genero genero){
        // TODO
    }

    // TODO 3 - apagar uma música
    // O quiz battle só usou deleteMany pra zerar tudo. Aqui a gente
    // quer apagar UMA específica.
    // O método chama-se col.deleteOne e o filtro é o mesmo eq("_id", ...)
    // que a gente já usa pra encontrar uma.
    public void delete(String id){
        // TODO
    }

    // TODO 4 - contar músicas por gênero (vai virar o PieChart)
    // O retorno esperado é um Map tipo:  { "Rock" -> 3, "Pop" -> 4, ... }
    //
    // Como fazer (jeito mais simples, sem stream):
    // 1 - cria um HashMap<String, Long> vazio
    // 2 - chama findAll() pra pegar a lista de músicas
    // 3 - pra cada música, pega o rotulo do genero (m.genero().rotulo())
    // 4 - usa  map.merge(rotulo, 1L, Long::sum)
    //     que faz: "se o rótulo não tá no map, coloca com 1.
    //              se já tá, soma 1 ao valor atual".
    // 5 - retorna o map
    public Map<String, Long> countByGenero(){
        return new HashMap<>();
    }

    // Soma a duração (em segundos) de uma lista de músicas.
    // Já pronto pra vocês usarem na aba de Playlist.
    // O stream().mapToInt(...) pega um campo int de cada objeto e
    // o .sum() soma tudo. É um for somador disfarçado.
    public static int somaDuracao(List<Musica> musicas){
        return musicas.stream().mapToInt(Musica::duracaoSegundos).sum();
    }
}
