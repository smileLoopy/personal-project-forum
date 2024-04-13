package com.personal.projectforum.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class PaginationService {

    private static final int BAR_LENTH = 5;

    public List<Integer> getPaginationBarNumbers(int currentPageNumer, int totalPages) {

        int startNumber = Math.max(currentPageNumer - (BAR_LENTH / 2),0);
        int endNumber = Math.min(startNumber + BAR_LENTH, totalPages);
        return IntStream.range(startNumber, endNumber).boxed().toList();
    }

    public int currentBarLength() {
        return BAR_LENTH;
    }
}
