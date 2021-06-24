package com.example.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author YL
 * @date 9:41 2021/6/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String networkId;

    private List<String> parentMessageIds;


}
