Последнее обновление: 10 апреля 2025  

Описание проекта:
Класс (его визуальная реализация) - За что отвечает
Package BasicPetsWind – экран "Питомцы" и его компоненты:
AddPetActivity.class (activity_add_pet.xml) - Добавление питомца в "список питомцев"

AddPetRecordActivity.class
(item_pet_record.xml)
                                                  Добавление записи в "список записей о питомце"
                                                  
LineChartView.class                                            Статистика компонентов 
                                                            "списка записей о питомце"
                                                            
Pet.class                                         Структура данных компонентов экрана "Питомцы"

PetActivity.class (activity_pet.xml)                            Главный экран "Питомцы"

PetAdapter.class (item_pet.xml)                                 Отображение питомца
PetDetailsActivity.class 
(activity_pet_details.xml)
                                                                Отображение питомца, информации о нём и "списка записей о питомце"

PetRecordsAdapter.class (item_pet_record.xml)                  Добавление новой записи о питомце
Package Interface – интерфейсы приложения
ItemClickListner.java                                     Нажатие кнопки "Добавить объявление" в экран "Объявления"
Package Market – экран "Объявления" и его компоненты
AddNewPetActivity.class 
(activity_add_new_pet.xml)
                                                                  Добавление нового питомца в "Объявления"
CategoryPetActivity.class 
(activity_category_pet.xml)
                                                                    Экран с выбором категории (типа) питомца
MarketActivity.class (app_bar_main.xml)                                     Главный экран "Объявлений"
Package Model – модели для внутренней части кода
LoginRequest.class                                      Запрос на получение данных о зарегистрированном пользователе с сервера
LoginResponse.class                                     Получение данных о зарегистрированном пользователе с сервера
Pet.class                                                Модель данных для "списка записей о питомце" для сервера
PetRecord.class                                         Модель данных для записи "списка записей о питомце" на сервер
Pets.class                                                 Модель данных для информации о питомце для "Объявлений"
Post.class                                                Модель данных для информации о питомце для "Публикаций"
Users.class                                                     Модель данных для информации о пользователе
Package Network – инструменты для сервера и связки с backend-ом приложения
ApiService.java                                                   Интерфейс для получения API с сервера
RetrofitClient.class                                               Получений данных с сервера и связь с ним

Package Posts – экран "Публикации" и его компоненты
AddPostActivity.class (activity_add_post.xml)                      Добавление публикации на экран "Публикации"
PostsActivity.class (activity_posts.xml)                                  Главный экран "Публикаций"
PostsAdapter.class                                                Структура для сохранения данных в публикации
Package Prevalent – работа с данными пользователя
Prevalent.class                                                     Структура сохранения данных пользователя
Package Profile - экран "Профиль" и его компоненты
ProfileUserActivity.class
(activity_profile_user.xml)
                                                                          Главный экран "Профиля"
Package PetViewHolder – структура данных для "Объявления"
PetViewHolder.class                                                   Сохранение данных "Объявлений"
Package Welcome – "Приветственный экран", экраны "Регистрации" и "Авторизации" и их компоненты
AdminDeletePostsActivity.class 
(activity_admin_delete_posts.xml)
                                                            Экран администратора для удаления и редактирования контента, публикуемого пользователями (публикации и объявления)
LoginActivity.class (activity_login.xml)                                    Экран "Авторизации"
MainActivity.class (activity_main.xml)                                    "Приветственный экран"
RegisterActivity.class (activity_register.xml)                              Экран "Регистрации"
Settings – экран "Настройки" и его компоненты
SettingsActivity.class (activity_settings.xml)                                Экран "Настройки"
