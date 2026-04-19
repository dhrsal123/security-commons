package io.cinema.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Cors {
    private List<String> allowedOrigins;
    private List<String> allowedHeaders;
    private List<String> allowedMethods;

}
