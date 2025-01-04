package org.example.springjdbc.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category {
    int id;
    String name;
    final List<Option> options = new ArrayList<>();

    public void addOption(Option option) {
        options.add(option);
    }
}
