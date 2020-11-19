package com.move.move.service;

import com.move.move.exception.MovesException;
import com.move.move.model.AuditProcess;
import com.move.move.model.Moves;
import com.move.move.repository.AuditProcessRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MovesService {

    private static final Logger LOG = LoggerFactory.getLogger(MovesService.class);
    private static final int MINIMUM_WEIGHT_BAG = 50;

    @Autowired
    AuditProcessRepository auditProcessRepository;

    public String calculateMoves(MultipartFile multipartFile, long executorId) throws IOException {
        List<String> movesPerDayList = new ArrayList<>();
        //Object which keep each day, with the weight items list to move
        Moves moves;
        File file;
        try {
            //Converts MultipartFile to a File
            file = new File(System.getProperty("java.io.tmpdir"), "movesTmp.txt");
            multipartFile.transferTo(file);
            //Process the input information to arrange it
            moves = readFile(file);
        } catch (NumberFormatException ne) {
            LOG.error("El archivo de entrada solo debe contener un número por cada línea");
            throw new MovesException(ne.getMessage() + " -- El archivo de entrada solo debe contener un número por cada línea --");
        } catch (Exception e){
            LOG.error("Hubo problemas con la lectura y/o escritura del archivo.");
            throw new MovesException(e.getMessage() + " -- Hubo problemas con la lectura y/o escritura del archivo --");
        }

        //List which keep the maximum amount of moves in a day
        moves.getMoves().forEach((k,v) -> movesPerDayList.add("Case #" + k + ": " + maxAmountMoves(v)));

        //Generate the output file and the method return the route where the file is
        String route = generateOutPutFile(movesPerDayList);

        //Save the audit process row in the database
        recordAuditProcess(executorId);

        return route;
    }

    public Moves readFile(File file) throws IOException {
        String line;
        Integer numStuffPerDay;
        Integer numWorkedDays;
        Moves moves = new Moves();
        //Configuration to read the file
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);
        //number of work days
        numWorkedDays = Integer.parseInt(bufferedReader.readLine());
        if(numWorkedDays < 1 || numWorkedDays >100) {
            throw new MovesException("El número de días de trabajo debe estar entre 1 y 500 días. " +
                    "Esta cantidad de días " + numWorkedDays + " no es permitido");
        }
        int day = 1;
        //Go around the file
        while ((line=bufferedReader.readLine())!= null) {
            List<Integer> listStuffPerDay = new ArrayList<>();
            //Extract the amount items to e moved in that particular day
            numStuffPerDay = Integer.parseInt(line);
            if(numStuffPerDay < 1 || numStuffPerDay >100) {
                throw new MovesException("La cantidad de elementos movilizados por día debe estar entre " +
                        "1 y 100 objetos. Esta cantidad de elementos " + numStuffPerDay + " no es permitido");
            }
            int i=1;
            //Go around the items to be moved in that day and save it in a particular list
            while (i <= numStuffPerDay){
                if ((line=bufferedReader.readLine())!= null) {
                    int weightItem = Integer.parseInt(line);
                    //Validate the weight item, which can be between 1 to 100
                    if (weightItem < 1 || weightItem >100) {
                        throw new MovesException("El peso de un objeto debe estar entre 1 y 100 libras. Este peso "
                                + weightItem + "lb no es permitido");
                    }
                    listStuffPerDay.add(weightItem);
                    i++;
                }
            }
            //Order the list descendingly
            listStuffPerDay.sort((o1, o2)-> o2.compareTo(o1));

            //Add a day and its weight item list
            moves.setMoves(day, listStuffPerDay);
            day++;
        }
        //Handle exceptions to close input file
        if (reader != null) {
            reader.close();
        }
        return  moves;
    }

    public int maxAmountMoves(List<Integer> listStuff){
        int counterMaxAmountMoves = 0;
        //Go around the list group items maximizing the moves
        while (listStuff.size()>0){
            //Each move is made with the greater weight item at the list first
            int lastStuffBag = listStuff.remove(0);
            //If an item weight is greater than 50, it counts like a move
            if(lastStuffBag < MINIMUM_WEIGHT_BAG){
                double minAmountStuffInBag = Math.ceil(MINIMUM_WEIGHT_BAG/lastStuffBag);
                if(listStuff.size() >= minAmountStuffInBag){
                    int i=1;
                    while(i<=minAmountStuffInBag){
                        int indexStuffRemove = listStuff.size()-1;
                        listStuff.remove(indexStuffRemove);
                        i++;
                    }
                    counterMaxAmountMoves++;
                }
            }else{
                counterMaxAmountMoves++;
            }
        }
        return counterMaxAmountMoves;
    }

    public String generateOutPutFile(List<String> movesPerDayList) throws IOException {
        //Create and write a file with the list of the final result
        String route = System.getProperty("java.io.tmpdir") + "moves.txt";
        File resultFile = new File(route);
        FileWriter fileWriter = new FileWriter(resultFile);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int j = 0; j< movesPerDayList.size(); j++){
            bufferedWriter.write(movesPerDayList.get(j));
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
        return route;
    }

    private void recordAuditProcess(long executorId) {
        //Save the record to the audit_process table
        AuditProcess ap = new AuditProcess(executorId, new Date());
        auditProcessRepository.save(ap);
    }
}
