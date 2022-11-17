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

import static ru.vsu.css.vorobcov_i_a.Main.ConsoleParams.CrudType.*;

public class Main {

    private static final PlayerDB playerDB = new PlayerDB();

    @Data
    @NoArgsConstructor
    static class ConsoleParams{
        enum CrudType{
            GET,
            UPDATE,
            DELETE,
            SAVE
        }
        private  CrudType crudType;
        private  Long id;
        private  String inputFile;
        private  String outputFile;
        private  boolean needHelp;
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
                        ConsoleParams.CrudType crudType;
                        try {
                            crudType = ConsoleParams.CrudType.valueOf(crudTypeString);
                        }catch (IllegalArgumentException e){
                            consoleParams.needHelp = true;
                            break;
                        }
                        consoleParams.crudType = crudType;
                        i++;
                    }
                }
            }
        }
        if(consoleParams.crudType == null){
            consoleParams.needHelp = true;
        }else if((UPDATE == consoleParams.getCrudType() || SAVE == consoleParams.getCrudType()) && consoleParams.inputFile == null){
            consoleParams.needHelp = true;
        }

        return consoleParams;
    }


    @SneakyThrows
    public static void main(String[] args) throws IOException {
        ConsoleParams consoleParams = parsParams(args);

        if(consoleParams.isNeedHelp()){
            showHelpInfo();
        }else{
            Long id = consoleParams.getId();
            if(consoleParams.crudType == GET){
                if(id == null){
                    //read all and if has file to file, if not - to console
                    List<Player> allPlayers = playerDB.readAll();
                    String outString = PlayerUtil.convertToString(allPlayers);
                    if(consoleParams.getOutputFile() == null){
                        System.out.println(outString);
                    }else{
                        FileOutputStream outputStream = new FileOutputStream(consoleParams.getOutputFile());
                        byte[] strToBytes = outString.getBytes();
                        outputStream.write(strToBytes);
                        outputStream.close();
                    }
                }else{
                    Player player = playerDB.readById(id);
                    String stringPlayer = PlayerUtil.convertToString(player);

                    if(consoleParams.getOutputFile() == null){
                        System.out.println(stringPlayer);
                    }else{
                        FileOutputStream outputStream = new FileOutputStream(consoleParams.getOutputFile());
                        byte[] strToBytes = stringPlayer.getBytes();
                        outputStream.write(strToBytes);
                        outputStream.close();
                    }
                    //read specific by id and if has file to file, if not - to console
                }

            }else if(consoleParams.crudType == UPDATE){
                if(id == null){
                    //update all - read from file
                }else{
                    //update specific by id - read from file
                }
            }else if(consoleParams.crudType == DELETE){
                if(id == null){
                    //delete all
                }else{
                    //delete specific by id
                }
            }else{
                //save operation from file
            }
        }
    }


    private static void showHelpInfo(){
        System.out.println("-cd <crud type> [-id <id>] -iF <file name> [-oF <file name>]");
        System.out.println("crud types: GET(without id will take all), UPDATE(id and inputFile is needed), SAVE(input file is needed), DELETE(without id will delete all)");
    }
}
