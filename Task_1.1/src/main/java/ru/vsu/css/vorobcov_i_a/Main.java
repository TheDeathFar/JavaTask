package ru.vsu.css.vorobcov_i_a;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import ru.vsu.css.vorobcov_i_a.database.PlayerDB;
import ru.vsu.css.vorobcov_i_a.models.Player;
import ru.vsu.css.vorobcov_i_a.util.PlayerUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static ru.vsu.css.vorobcov_i_a.Main.ConsoleParams.Crud.*;

public class Main {

    private static final PlayerDB playerDB = new PlayerDB();

    @Data
    @NoArgsConstructor
    static class ConsoleParams{
        enum Crud{
            GET,
            UPDATE,
            DELETE,
            SAVE
        }
        private  Crud crud;
        private  Long id;
        private  String inputFile;
        private  String outputFile;
        private  boolean needHelp = false;
        private  boolean isExit = false;
    }
    // -cd GET -id 3 -iF players.json -oF output.txt
    private static ConsoleParams parsParams(String[] args){
        ConsoleParams consoleParams = new ConsoleParams();
        if(args.length == 0){
            consoleParams.needHelp = true;
        }else{
            for(int i = 0;i < args.length; i++){
                if(args[i].equals("-cd")){
                    if(i + 1 >= args.length){
                        consoleParams.needHelp = true;
                        break;
                    }else{
                        String crudTypeString = args[i+1];
                        ConsoleParams.Crud crudType;
                        try {
                            crudType = ConsoleParams.Crud.valueOf(crudTypeString);
                        }catch (IllegalArgumentException e){
                            consoleParams.needHelp = true;
                            break;
                        }
                        consoleParams.crud = crudType;
                        i++;
                    }
                }else if(args[i].equals("-id")){
                    if(i + 1 >= args.length){
                        consoleParams.needHelp = true;
                        break;
                    }else{
                        long id;
                        try{
                            id = Long.parseLong(args[i+1]);
                        }catch (NumberFormatException e){
                            consoleParams.needHelp = true;
                            break;
                        }
                        consoleParams.id = id;
                    }
                }
                else if(args[i].equals("-iF")){
                    if(i + 1 >= args.length){
                        consoleParams.needHelp = true;
                        break;
                    }else{
                        consoleParams.inputFile = args[i+1];
                        i++;
                    }
                } else if(args[i].equals("-oF")){
                    if(i + 1 >= args.length){
                        consoleParams.needHelp = true;
                        break;
                    }else{
                        consoleParams.outputFile = args[i+1];
                        i++;
                    }
                }else if(args[i].equals("--help")){
                    consoleParams.needHelp = true;
                }else if(args[i].equals("--exit")){
                    consoleParams.isExit = true;
                }
            }
        }
        if(consoleParams.crud == null){
            consoleParams.needHelp = true;
        }else if((UPDATE == consoleParams.getCrud() || ConsoleParams.Crud.SAVE == consoleParams.getCrud()) && consoleParams.inputFile == null){
            consoleParams.needHelp = true;
        }

        return consoleParams;
    }


    @SneakyThrows
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("Next command: ");
            args = sc.nextLine().split(" ");
            ConsoleParams consoleParams = parsParams(args);
            if(consoleParams.isNeedHelp()){
                showHelpInfo();
            }else if(consoleParams.isExit()){
                break;
            }else{
                Long id = consoleParams.getId();
                if(consoleParams.crud == GET){
                    if(id == null){
                        //read all and if has file to file, if not - to console
                        List<Player> allPlayers = playerDB.readAll();
                        String outString = PlayerUtil.convertToString(allPlayers);
                        if(consoleParams.getOutputFile() == null){
                            System.out.println(outString);
                        }else{
                            try(FileOutputStream outputStream = new FileOutputStream(consoleParams.getOutputFile())){
                                byte[] strToBytes = outString.getBytes();
                                outputStream.write(strToBytes);
                            }
                        }
                    }else{
                        Player player = playerDB.readById(id);
                        String stringPlayer = PlayerUtil.convertToString(player);
                        if(consoleParams.getOutputFile() == null){
                            System.out.println(stringPlayer);
                        }else{
                            try(FileOutputStream outputStream = new FileOutputStream(consoleParams.getOutputFile())) {
                                byte[] strToBytes = stringPlayer.getBytes();
                                outputStream.write(strToBytes);
                            }
                        }
                    }

                }else if(consoleParams.crud == UPDATE){
                    if(id == null){
                        showHelpInfo();
                        //update all - read from file
                    }else{
                        if(consoleParams.getInputFile() == null){
                            showHelpInfo();
                        }else{
                            Player player = PlayerUtil.readOneFromFile(consoleParams.getInputFile());
                            playerDB.update(id, player);
                        }
                        //update specific by id - read from file
                    }
                }else if(consoleParams.crud == DELETE){
                    if(id == null){
                        //delete all
                        playerDB.deleteALl();
                    }else{
                        //delete specific by id
                        playerDB.delete(id);
                    }
                }else{
                    if(consoleParams.getInputFile() == null){
                        showHelpInfo();
                    }else{
                        List<Player> players = PlayerUtil.readFromFile(consoleParams.getInputFile());
                        playerDB.saveAll(players);
                    }
                    //save operation from file
                }
            }
        }
    }


    private static void showHelpInfo(){
        System.out.println("-cd <crud type> [-id <id>] -iF <file name> [-oF <file name>]");
        System.out.println("crud types: GET(without id will take all), UPDATE(id and inputFile is needed), SAVE(input file is needed), DELETE(without id will delete all)");
    }
}
