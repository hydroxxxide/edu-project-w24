package kg.backend.tinder.service;

import kg.backend.tinder.dto.FormDto;
import kg.backend.tinder.model.Person;
import kg.backend.tinder.model.User;
import kg.backend.tinder.repository.PersonRepository;
import kg.backend.tinder.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TinderService {
    private final UserRepository userRepository;
    private final PersonRepository personRepository;

    public void saveForm(FormDto request) {
        User user = userRepository.getPersonUser(request.getUserId()).orElseThrow();
        Person person = new Person();
        if (user.getPerson() != null) {
            person = user.getPerson();
        }

        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setAge(request.getAge());
        person.setHobby(request.getHobby());
        person.setPersonalInformation(request.getPersonalInfo());
        person.setRequirements(request.getRequirements());
        personRepository.save(person);
    }
}
