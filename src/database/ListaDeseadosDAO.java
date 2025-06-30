package database;

import java.util.List;

public interface ListaDeseadosDAO {
    List<Integer> getVideojuegosIds(int listaId);
    boolean addVideojuegoToLista(int listaId, int videojuegoId);
    boolean removeVideojuegoFromLista(int listaId, int videojuegoId);
}