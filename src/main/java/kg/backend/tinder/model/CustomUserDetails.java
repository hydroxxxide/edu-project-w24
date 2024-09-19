package kg.backend.tinder.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    Collection<? extends GrantedAuthority> authorities;


    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = user.getAuthorities();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    String index = """
            что такое индекс? как работает?
            
            - индекс, это объект в бд, являющийся структурой данных (типо как коллекции в джаве) из отсортированных ссылок на индексируемое поле (столбец) для более быстрого доступа к данным при поиске или фильтрации по индексируемому полю (столбцу)
            
            - при создании индекса по столбцу (к примеру first_name), БД начинает структированно создавать ссылки на каждую запись в таблице с этим столбцом и добавлять их в индекс, следуя выбранной архитектуре индекса
            
            CREATE INDEX idx_users_first_name //имя индекса
            ON users //имя таблицы\s
            USING btree //структура данных для индекса - бинарное древо
            (first_name); // поле в таблице users, которое мы собираемся добавить в индекс
            
            - всего существует около шести различных индексов, дефолтный из которых - btree
            
            - при поиске в таблице по полю, которое имеет индекс (например мы пытаемся найти человека с именем Левронтий), БД не проходится по всей таблице (table full scan), а обращается к индексу этого поля, в котором содержатся все поля в отсортированном виде, за счет чего поиск происходит во много раз быстрее
            грубо говоря, БД проходится по копии таблицы, только в отсортированном виде которая содержит только поля добавленные в индекс, благодаря чему поиск и фильтрация по индексируемым полям происходит мгновенно
            
            - при добавлении в таблицу новой записи, добавляется и новая ссылка в индексе, например:
            если мы создаем индекс в таблице users на столбце first_name, то при регистрации какого-нибудь Вити, запись такого пользователя попадет в таблицу, и индексу нужно будет добавить новую ссылку имя Вити в правильное место для поддержки алфавитного порядка, как если бы библиотекарь после новой поставки книг, разносил бы их по правильным полкам
            
            какие "подводные" у индексов?
            
            - если в таблице не очень много записей, то и создавать индекс для нее нет смысла, ибо БД быстрее пройдется по всей таблице, чем использует индекс
            
            - частое обновление таблицы (добавление, удаление, обновление записей) триггерит индекс на обновление ссылок (см. последний пункт для чего нужны индексы), это затрачивает время на обработку DML команд в БД (INSERT, DELETE, UPDATE)
            
            - много индексов - много затраченного места в памяти
            
            - перегрузка различными индексами в таблице может негативно сказаться на скорости работы при сложных запросах
            """;
}
