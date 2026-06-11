package br.cesul.catalogomusical.ui;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

// Aba "Estatísticas".
// PieChart é o gráfico de pizza do JavaFX. A gente alimenta ele
// com PieChart.Data(rotulo, valor) — um pra cada fatia.

// O grafico em si ja está criado aqui. O que falta é o refresh():
// chamar countByGenero() do DAO e jogar cada par (rotulo, qtd) no
// chart.

public class StatsPane extends BorderPane implements Refreshable {

    // PieChart fica em um campo da classe pra gente conseguir mexer
    // nele depois (no refresh).
    private final PieChart chart = new PieChart();

    public StatsPane(){
        setPadding(new Insets(10));
        chart.setTitle("Músicas por gênero");
        chart.setLegendVisible(true);

        setCenter(new VBox(new Label("Estatísticas do catálogo"), chart));

        // Primeira carga
        refresh();
    }

    // TODO - popular o PieChart
    // Passos:
    // 1 - chart.getData().clear()   -> apaga o gráfico anterior
    // 2 - cria um MusicaDao (new MusicaDao()).
    // 3 - chama dao.countByGenero() -> isso devolve um Map<String, Long>
    //     tipo { "Rock" -> 3, "Pop" -> 4, ... }
    // 4 - itera o map. Pra cada entrada (rotulo, qt) faz:
    //         chart.getData().add(new PieChart.Data(rotulo, qt));
    //     (pode usar map.forEach((rotulo, qt) -> ...) que ficou bonitinho)
    @Override
    public void refresh(){
        chart.getData().clear();
        // TODO
    }
}
