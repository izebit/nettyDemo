package ru.izebit.services;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SuperServiceImpl implements SuperService {

    @Setter
    @Autowired
    private ResultCache<Double> cache;

    //симметричные элементы относительно середины пирамиды имеют одинаковые значения
    private static int getIndex(int level, int index) {
        return index > level / 2 ? level - index : index;
    }

    @Override
    public Double getHumanEdgeWeight(int level, int index) {
        if (level < 0 || index < 0 || level < index) return null;

        //пробуем найти в кэше
        Optional<Double> cachedResult = cache.getCachedResult(level, index);
        if (cachedResult.isPresent()) return cachedResult.get();

        //вычисление значения
        double[][] results = new double[2][level / 2 + 1];
        LABEL:
        for (int i = 0; i <= level; i++) {
            for (int j = 0; j <= i; j++) {
                getWeightForCell(i, j, results);
                if (j == index && i == level) break LABEL;
            }
        }

        double result = results[level % 2][getIndex(level, index)];
        cache.putCachedResult(level, index, result);
        return result;
    }

    private double getWeightForCell(int level, int index, double[][] results) {
        if (level == 0 && index == 0) return results[0][0] = 0;

        int arrayIndex = getIndex(level - 1, index - 1);
        double leftResult = 0;
        if (arrayIndex >= 0 && arrayIndex <= level - 1)
            leftResult = results[(level - 1) % 2][arrayIndex] + 50;

        arrayIndex = getIndex(level - 1, index);
        double rightResult = 0;
        if (arrayIndex >= 0)
            rightResult = results[(level - 1) % 2][getIndex(level - 1, index)] + 50;

        double result = leftResult / 2 + rightResult / 2;

        arrayIndex = getIndex(level, index);
        return results[level % 2][arrayIndex] = result;
    }
}
