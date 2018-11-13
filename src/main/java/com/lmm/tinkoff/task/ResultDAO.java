package com.lmm.tinkoff.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.stream.Collectors;

@Component
public class ResultDAO {

    private static final String INSERT_RESULT_SQL = "insert into find_number_results " +
            "(code, number, filenames, error) values (?,?,?,?)";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ResultDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertResult(BigInteger number, Result result) {
        jdbcTemplate.update(INSERT_RESULT_SQL,
                result.getCode().value(),
                number,
                convertFileNamesToString(result.getFileNames()),
                result.getError());
    }

    private String convertFileNamesToString(FileNamesList fileNamesList) {
        if(fileNamesList == null) {
            return "";
        }
        return fileNamesList.getFileName().stream().collect(Collectors.joining(","));
    }

}
