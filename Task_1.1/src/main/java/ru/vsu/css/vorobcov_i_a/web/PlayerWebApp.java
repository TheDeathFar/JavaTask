package ru.vsu.css.vorobcov_i_a.web;

import lombok.SneakyThrows;
import ru.vsu.css.vorobcov_i_a.database.PlayerDB;
import ru.vsu.css.vorobcov_i_a.models.Player;
import ru.vsu.css.vorobcov_i_a.util.PlayerUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/player/*")
public class PlayerWebApp extends HttpServlet {
    private final static PlayerDB playerDB = new PlayerDB();

    @SneakyThrows
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        if(split.length == 2){
            out.println(PlayerUtil.convertToString(playerDB.readAll()));
        }else if(split.length == 3){
            Long id = Long.valueOf(split[2]);
            out.println(PlayerUtil.convertToString(playerDB.readById(id)));
        }else{
            resp.sendError(400);
        }
        out.flush();
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String stringPlayers = readDataFromRequest(req);
        List<Player> players = PlayerUtil.readAllPlayersFromString(stringPlayers);
        playerDB.saveAll(players);
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String stringPlayer = readDataFromRequest(req);
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        if(split.length != 3){
            resp.sendError(400);
        }else{
            Long id = Long.valueOf(split[2]);
            Player player = PlayerUtil.readPlayerFromString(stringPlayer);
            playerDB.update(id, player);
        }
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();
        String[] split = requestURI.split("/");
        if(split.length == 2){
            playerDB.deleteALl();
        }else if(split.length == 3){
            Long id = Long.valueOf(split[2]);
            playerDB.delete(id);
        }else{
            resp.sendError(400);
        }
    }

    private String readDataFromRequest(HttpServletRequest req){
        StringBuilder jb = new StringBuilder();
        String line = null;
        try {
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) { /*report an error*/ }

        return jb.toString();
    }
}
