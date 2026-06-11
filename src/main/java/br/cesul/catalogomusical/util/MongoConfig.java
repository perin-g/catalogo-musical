package br.cesul.catalogomusical.util;

// Mesmo esquema do quiz battle:
// - uma conexão única reaproveitada pra qualquer DAO
// - métodos static que entregam a coleção
// - um seed no início pra ja ter dados quando rodar

// Banco usado: catalogo_musical
// Coleções: musicas, playlists

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;

public final class MongoConfig {

    // Conexão única. Abrir conexão com banco é operação cara,
    // por isso a gente faz UMA vez e usa pra sempre.
    private static final MongoClient CLIENT =
            MongoClients.create("mongodb://localhost:27017");

    private static final String DB_NAME = "catalogo_musical";

    private static MongoDatabase db(){
        return CLIENT.getDatabase(DB_NAME);
    }

    // Helpers pra cada coleção que vamos usar.
    // Cada DAO chama um desses pra pegar a coleção dele.
    public static MongoCollection<Document> musicas(){
        return db().getCollection("musicas");
    }

    public static MongoCollection<Document> playlists(){
        return db().getCollection("playlists");
    }

    // Roda no start do MainApp. Se a coleção já tem
    // alguma coisa dentro, não faz nada — assim a gente
    // não duplica dados a cada execução.
    public static void seedMusicasIfEmpty(){
        if (musicas().countDocuments() > 0) return;

        musicas().insertMany(Arrays.asList(
                nova("Bohemian Rhapsody",      "Queen",            "A Night at the Opera", 354, "ROCK"),
                nova("Smells Like Teen Spirit","Nirvana",          "Nevermind",            301, "ROCK"),
                nova("Billie Jean",            "Michael Jackson",  "Thriller",             294, "POP"),
                nova("Shape of You",           "Ed Sheeran",       "Divide",               233, "POP"),
                nova("Garota de Ipanema",      "Tom Jobim",        "Getz/Gilberto",        162, "MPB"),
                nova("Águas de Março",         "Elis Regina",      "Elis & Tom",           211, "MPB"),
                nova("Levels",                 "Avicii",           "Levels",               205, "ELETRONICO"),
                nova("Für Elise",              "Beethoven",        "Bagatelles",           180, "CLASSICO"),
                nova("Take Five",              "Dave Brubeck",     "Time Out",             324, "JAZZ"),
                nova("Lose Yourself",          "Eminem",           "8 Mile",               326, "RAP")
        ));
    }

    // Helper só pra deixar o seed mais limpo de ler.
    // Os campos batem com os atributos do record Musica.
    private static Document nova(String titulo, String artista, String album,
                                 int duracao, String genero){
        return new Document()
                .append("titulo", titulo)
                .append("artista", artista)
                .append("album", album)
                .append("duracaoSegundos", duracao)
                .append("genero", genero);
    }

    // Construtor privado pra ninguém fazer "new MongoConfig()" por engano.
    private MongoConfig(){}
}
