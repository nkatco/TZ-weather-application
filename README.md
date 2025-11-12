Архитектура проекта - MVVM
:app
    ├─> :feature:main (Compose UI + VM) тут главная страница 
    ├─> :domain (интерфейсы repo + use cases) 
    └─> :data:repo (реализации интерфейсов) 
        ├─> :data:remote (Retrofit) тут сеть 
        └─> :data:local (Room) тут кэш для запросов из сети

:feature:main ──> :domain
:data:repo ──> :domain, :data:remote, :data:local
:data:remote ──> (ничего Android-UI), только Retrofit
:data:local ──> (только Room)