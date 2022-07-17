# java-filmorate
## Это репозиторий проекта "Рейтинг фильмов"

Наше приложение **умеет**:
1. Создавать информацию о фильмах и пользователях.
2. Фиксировать лайки пользователей к фильмам и добавлять друзей пользователям.
3. ПОлучать список самых популярных фильмах.

Приложение написано на Java с использованием Spring Boot. Пример кода:
```java
@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
```


![ER-модель данных](https://github.com/e-popov-lst/java-filmorate/blob/db-erd-schema/filmorate_erd.jpg)

Пример получения списка друзей :
```sql
SELECT u.name
FROM friend f
INNER JOIN user u ON u.user_id = CASE
                                     WHEN f.initiator = :userId THEN f.candidate
                                     ELSE f.initiator
                                 END
WHERE f.is_confirmed = TRUE
  AND (f.initiator = :userId
       OR f.candidate = :userId);
```

------
О том, как научиться создавать такие приложения, можно узнать в [Яндекс-Практикуме](https://practicum.yandex.ru/java-developer/ "Тут учат Java!") 
