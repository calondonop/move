package com.move.move.service;

import com.move.move.repository.AuditProcessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MovesServiceTest {

    @InjectMocks
    @Spy
    private MovesService movesService = new MovesService();

    @Mock
    private AuditProcessRepository auditProcessRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(MovesServiceTest.class);
    }

    MockMultipartFile file
            = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "1\n4\n30\n30\n1\n1".getBytes()
    );

    private long id = 123;

    @Test
    public void successfullyCalculate() throws IOException {

        String route = movesService.calculateMoves(file, id);

        assertNotNull(route);
        assertTrue(route.contains("moves.txt"));
    }

    @Test
    public void maxAmountMovesTest() throws IOException {
        //arrange
        movesService.calculateMoves(file, id);

        ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(movesService,times(1)).generateOutPutFile(argumentCaptor.capture());
        verify(auditProcessRepository,times(1)).save(any());
        assertEquals(1, argumentCaptor.getValue().size());
    }

    @Test
    public void maxAmountValuesTest(){
        List<Integer> listItem = new ArrayList<>();
        listItem.add(30);
        listItem.add(30);
        listItem.add(1);
        listItem.add(1);

        //act
        int maxMovesWithItems = movesService.maxAmountMoves(listItem);

        //assert
        assertTrue(maxMovesWithItems == 2);

        //arrange
        listItem.clear();
        listItem.add(70);
        //act
        maxMovesWithItems = movesService.maxAmountMoves(listItem);
        //assert
        assertTrue(maxMovesWithItems == 1);

        //arrange
        listItem.clear();
        listItem.add(40);
        //act
        maxMovesWithItems = movesService.maxAmountMoves(listItem);
        //assert
        assertTrue(maxMovesWithItems == 0);
    }

    @Test
    public void generateOutPutFileTest() throws IOException {
        //arrange
        List<String> movesPerDay = new ArrayList<>();
        movesPerDay.add("Case #1: 2");
        movesPerDay.add("Case #2: 1");
        movesPerDay.add("Case #3: 2");
        movesPerDay.add("Case #4: 3");
        movesPerDay.add("Case #5: 8");

        //act
        String route = movesService.generateOutPutFile(movesPerDay);

        //assert
        assertNotNull(route);
    }
}
