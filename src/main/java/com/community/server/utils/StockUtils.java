package com.community.server.utils;

import com.community.server.dto.StockDto;
import com.community.server.entity.StockEntity;

import java.math.RoundingMode;
import java.util.List;

public class StockUtils {

    public StockDto getStock(List<StockEntity> list) {

        if (list.size() == 0) {
            return null;
        }

        StockEntity stockFirst = list.get(0);
        StockEntity stockLast = list.get(list.size() - 1);

        StockDto stockDto = new StockDto();
        stockDto.setCost(stockLast.getCost());
        stockDto.setProcent(
                stockFirst.getCost().compareTo(stockLast.getCost()) > 0 ?
                        stockFirst.getCost().divide(stockLast.getCost(), 2, RoundingMode.HALF_UP).floatValue() :
                        stockLast.getCost().divide(stockFirst.getCost(), 2, RoundingMode.HALF_UP).floatValue());

        stockDto.setMuch(stockFirst.getCost().compareTo(stockLast.getCost()) > 0 ? stockFirst.getCost().subtract(stockLast.getCost()) : stockLast.getCost().subtract(stockFirst.getCost()));
        stockDto.setUpper(stockFirst.getCost().compareTo(stockLast.getCost()) > 0);
        return stockDto;
    }

}
