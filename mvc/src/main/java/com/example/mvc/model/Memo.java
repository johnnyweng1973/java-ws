package com.example.mvc.model;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Memo {
    @Id
    private long id;
    private String title;
    private String description;
    private String category;
    private String content;
}
