# Unravelling
## Игра, цель которой *РАСПУТАТЬ* линии, связывающие шары

### Реализованные части:
- Сервер
- Андроид/виндовс приложение
- Веб приложение

### Примененные технологии:
1) Сервер - `Kotlin`, с использование фреймворка `Javalin` на вебсокетах

   и подключением к б.д. с помощью `MySql`
   
2) Андроид - `Kotlin`, с использованием `JetpackCompose`
   > `JetpackCompose` - фреймворк реактивного программирования для `Kotlin`, позволяющий сразу реализовать дерево UI,
   > и создать удобные точки данных(стейты), которые заставляют рендериться части UI, использующие стейты, когда стейты меняются,
   > а также обрабатывать данные при изменении выбранного набора стейтов, например запрашивать данные с сервера

   Подключение к серверу реализовано с помощью `Ktor`
   
3) Веб - `React` + `Typescript`, подключение к серверу реализовано с помощью `WebSocket`
### Импорты и зависимости:
 - **Ktor**:
   ```
       implementation("io.ktor:ktor-client-websockets:2.2.2")
       implementation("io.ktor:ktor-client-cio:2.2.2")
   ```
 - **Javalin**
   ```
       <dependency>
            <groupId>io.javalin</groupId>
            <artifactId>javalin</artifactId>
            <version>5.6.1</version>
       </dependency>
   ```
- **MySql**:
  ```
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
  ```
## Основные Скриншоты:
![изображение](https://github.com/Zeredan/Unravelling/assets/165821992/3f07c90f-2440-4ade-98b7-21fb2b312241)
![изображение](https://github.com/Zeredan/Unravelling/assets/165821992/ead78453-8cb5-4602-8344-2583e5b2c4c2)
![изображение](https://github.com/Zeredan/Unravelling/assets/165821992/e53ed8ae-4178-46c3-96df-47854017edca)
![изображение](https://github.com/Zeredan/Unravelling/assets/165821992/08c3a0e0-6bda-422f-89c8-a3b8d88aad04)


