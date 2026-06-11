package br.cesul.catalogomusical.ui;

// Toda aba dessa tela precisa recarregar seus dados
// quando o usuário clica nela (porque enquanto ele tava
// em outra aba, ele pode ter mexido no banco).

// Em vez de cada aba ter um método com nome diferente
// (recarregarMusicas, recarregarPlaylists, atualizarChart...),
// todas implementam ESTA interface e têm um refresh() só.

// Aí no MainApp eu não preciso saber qual aba é qual: chamo
// o refresh() pra qualquer uma e cada uma sabe se atualizar.

// É a mesma ideia de interface que a gente viu com Urgente
// no PetShop: contrato comum + implementações diferentes.

public interface Refreshable {
    void refresh();
}
