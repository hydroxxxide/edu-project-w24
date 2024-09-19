package kg.backend.tinder.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FormDto {

    private long userId;
    private String firstName;
    private String lastName;
    private int age;
    private String hobby;
    private String personalInfo;
    private String requirements;

}
