package br.cesul.catalogomusical.ui;

import br.cesul.catalogomusical.dao.MusicaDao;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Aba "Estatísticas".
// PieChart é o gráfico de pizza do JavaFX. A gente alimenta ele
// com PieChart.Data(rotulo, valor) — um pra cada fatia.

// A agregação (quantas músicas por gênero) acontece no MusicaDao.
// Aqui a gente só pega o Map<String, Long> pronto e joga no chart.

public class StatsPane extends BorderPane implements Refreshable {

    private final MusicaDao dao = new MusicaDao();

    // PieChart fica em um campo da classe pra gente conseguir mexer
    // nele depois (no refresh).
    private final PieChart chart = new PieChart();

    public StatsPane(){
        setPadding(new Insets(10));

        chart.setTitle("Músicas por gênero");
        chart.setLegendVisible(true);

        setCenter(new VBox(new Label("Estatísticas do catálogo"), chart));

        refresh();
    }

    // Recarrega o gráfico. O countByGenero do DAO já devolve o map
    // certinho:  { "Rock" -> 3, "Pop" -> 4, ... }
    @Override
    public void refresh(){
        chart.getData().clear();
        dao.countByGenero().forEach((rotulo, qt) ->
                chart.getData().add(new PieChart.Data(rotulo, qt)));
    }
}
