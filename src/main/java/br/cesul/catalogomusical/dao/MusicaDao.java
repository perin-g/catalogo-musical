package br.cesul.catalogomusical.dao;

// DAO da coleção 'musicas'.
// Mesmo papel do PlayerDao do quiz: converter Document <-> Musica
// e expor os métodos que a UI pode chamar.

// Esta é a versão de referência (piloto). Tudo já implementado.
// Compare com o seu skeleton pra entender o que faltava.

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
                .append("genero",  genero.name()));
    }

    // Busca várias músicas pelo id de uma vez.
    // Usado pela aba de playlist: tenho uma lista de IDs (musicaIds)
    // e quero trazer todos os Musica em uma ida só ao banco.
    // Filters.in("_id", lista) é o "where _id IN (...)" do mongo.
    public List<Musica> findByIds(List<String> ids){
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        List<ObjectId> objIds = new ArrayList<>();
        for (String s : ids) objIds.add(new ObjectId(s));

        List<Musica> list = new ArrayList<>();
        for (Document d : col.find(in("_id", objIds))){
            list.add(toMusica(d));
        }
        return list;
    }

    // Praticamente igual ao findByCategoria do QuestionDao do quiz.
    public List<Musica> findByGenero(Genero genero){
        List<Musica> list = new ArrayList<>();
        for (Document d : col.find(eq("genero", genero.name()))){
            list.add(toMusica(d));
        }
        return list;
    }

    // Atualiza vários campos de uma música.
    // Filtro: eq("_id", new ObjectId(id))    -> qual documento mexer
    // Alteração: $set com TODOS os campos    -> o que vai mudar
    //
    // O $set é o operador do mongo que substitui o valor dos campos
    // citados. Os outros campos do documento ficam como estavam.
    public void update(String id, String titulo, String artista, String album,
                       int duracaoSegundos, Genero genero){
        col.updateOne(
                eq("_id", new ObjectId(id)),
                new Document("$set", new Document()
                        .append("titulo",          titulo)
                        .append("artista",         artista)
                        .append("album",           album)
                        .append("duracaoSegundos", duracaoSegundos)
                        .append("genero",          genero.name()))
        );
    }

    // Apaga UMA música pelo id.
    // O quiz só tinha o deleteMany pra zerar tudo. Aqui a gente
    // quer apagar uma específica, então é deleteOne com filtro
    // no _id (mesma forma de eq que usamos em updates).
    public void delete(String id){
        col.deleteOne(eq("_id", new ObjectId(id)));
    }

    // Conta quantas músicas por gênero. Resultado:
    //   { "Rock" -> 3, "Pop" -> 4, "MPB" -> 3, ... }
    // É isso que o PieChart vai consumir lá na StatsPane.
    //
    // Estratégia: usei um Map e o método merge.
    //   merge(chave, valorInicial, funcDeCombinacao)
    // funciona assim: se a chave ainda não tá no map, joga ela
    // com o valorInicial (1). Se ja tá, aplica a função pra combinar
    // (Long::sum -> soma o valor atual com 1).
    //
    // Resultado final: o map fica com "Rock" -> qtas vezes Rock apareceu.
    public Map<String, Long> countByGenero(){
        Map<String, Long> contagem = new HashMap<>();
        for (Musica m : findAll()){
            String rotulo = m.genero().rotulo();
            contagem.merge(rotulo, 1L, Long::sum);
        }
        return contagem;
    }

    // Soma a duração (em segundos) de uma lista de músicas.
    // Usado na aba de Playlist pra mostrar "duração total".
    // stream().mapToInt(...) extrai um int de cada objeto e
    // .sum() soma tudo. É um for somador mais curto.
    public static int somaDuracao(List<Musica> musicas){
        return musicas.stream().mapToInt(Musica::duracaoSegundos).sum();
    }
}
