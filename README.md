<h1>Описание итоговой аттестации (REDDIT)</h1>
<h2>Задание </h2>
<h3>Цель задания и описание</h3>
Вам предстоит разработать мобильное приложение-аналог Reddit. 
<br>
Это приложение, которое позволяет зарегистрированным пользователям размещать ссылки на понравившуюся информацию в интернете, находить интересный контент и обсуждать его.
<br><a href="https://www.reddit.com/dev/api/#GET_api_v1_collections_collection">
Полное описание API</a>
<br><a href="https://www.figma.com/file/th9m6fDtRtFXxkGTsB2FZd/%D0%90%D0%BD%D0%B0%D0%BB%D0%BE%D0%B3-Reddit?node-id=13%3A0">Прототип Figma</a>
<br>
Общий принцип работы с протоколом Oaoth описан в инструкции на примере сервиса unsplash. Вы можете переложить алгоритм работы с протоколом, адаптировав его к API сервиса reddit. Особенности его работы описаны в документации к API.


<h4>Сценарии использования:</h4>
<li>Пользователь знакомится с основными функциями приложения на экране онбординга
<li>Онбординг отображается сразу при первом запуске приложения.
<li>Авторизация
<ul><li>А) Экран авторизации отображается всегда следующим экраном после онбординга.
<li>Б) Пользоваться приложением без авторизации нельзя.</ul>
<br>
API
<br><a href="https://github.com/pratik98/Reddit-OAuth">
Аутентификация
<br><a href="https://github.com/openid/AppAuth-Android">
AppAuth for Android</a>


<h4>Посмотреть вкладку «Сабреддиты»</h4>
<li>Вверху строка поиска, переключатель «Новое/Популярное».
<li>Предлагать возможность подписки прямо в списке сабреддитов.
<li>По нажатию на сабреддит открывать описание сабреддита.


<h4>Посмотреть сабреддит</h4>
<li>В сабреддите открывать сразу список комментариев.
<li>При клике на комментарий открыть его.
<li>Вверху окна зафиксировать название сабреддита и кнопку «Инфо», открывающую описание сабреддита.


<h4>Посмотреть описание сабреддита</h4>
<li>Общая информация о сабреддите.
<li>Кнопки:<ul>
<li>«Подписка/отписка на сабреддит».
<li>«Поделиться».</ul>


<h4>Посмотреть комментарии</h4>
<li>Автор, время, текст комментария.
<li>Кнопки:
<ul><li>«Сохранение».
<li>«Локальная загрузка».
<li>Кнопки для голосования.</ul>
<li>Ниже список первой страницы сообщений для комментария, после него кнопка «Показать всё». Кнопка ведёт на отдельную страницу сообщений с пагинацией.


<h4>Посмотреть профиль юзера</h4>
<li>Открывает профиль любого юзера.
<li>Краткая информация.
<li>Кнопка «Зафрендиться».
<li>Комментарии.


<h4>Посмотреть вкладку «Избранное»</h4>
<li>Вверху два переключателя: «Сабреддиты»/«Комментарии все»/«Сохранённые».
<li>Ниже списки сабреддитов и комментариев.
<li>Переходы при сохранённом:
<ul>
<li>Пытаться загрузить онлайн.
<ul>
<li>Если информация доступна, отображаем.
<li>Нет ― пишем, что информация недоступна или удалена.</ul>
<li>Пытаться загрузить локально.
<li>Пишем, что информация недоступна.</ul>


<h4>Добавить в друзья</h4>
<li>Можно добавить любого юзера.
<li>Список друзей лежит в профиле.


<h4>Посмотреть вкладку «Профиль»</h4>
<li>Кнопка разлогина.
<li>Описание профиля.
<li>Кнопка «Очистить сохранённые».
<li>Список друзей.
