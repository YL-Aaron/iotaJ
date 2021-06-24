package com.example.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YL
 * @date 16:11 2021/6/2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Essence {

    private Integer type;

    private List<Input> inputs;

    private List<Output> outputs;

    private Payload payload;
}
