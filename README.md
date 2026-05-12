# Pentaho Report Designer 3.9.1 — UI fork

Форк интерфейса **Pentaho Report Designer (PRD) CE 3.9.1 GA**: современный внешний вид (FlatLaf), экран загрузки, фон рабочей области, приветственное окно, настройки темы. Исходная платформа и лицензия — у проекта Pentaho (LGPL).

**Автор форка:** Dmitriy Shemyakin ([@DmitriyShemyakin](https://github.com/DmitriyShemyakin)).

Репозиторий: [https://github.com/DmitriyShemyakin/pentaho-report-designer-3.9.1-fork](https://github.com/DmitriyShemyakin/pentaho-report-designer-3.9.1-fork)

## Что внутри

- `prd-source-3.9.1-GA/` — исходники PRD (и патчи в этом форке).
- `pre-classic-source-3.9.1-GA/` — связанные исходники движка (импорт из upstream).
- `pom.xml` — сборка **overlay JAR** с переопределёнными классами UI и копированием `messages*.properties`.
- `tools/setup-prd-runtime.ps1` — загрузка и распаковка официального `prd-ce-3.9.1-GA.zip` в `runtime/` (не коммитится).
- `tools/run-prd.ps1` — запуск PRD с форк-JAR и FlatLaf первыми в classpath.

## Требования

- **Windows** (скрипты на PowerShell; запуск на других ОС — вручную собрать classpath по аналогии).
- **JDK** 8+ (проверено с JDK 21), `JAVA_HOME` желательно задать явно.
- **Apache Maven** 3.6+.
- **curl.exe** (обычно есть в Windows 10/11) — для скачивания рантайма.

## Быстрый старт

Из корня репозитория:

```powershell
.\tools\setup-prd-runtime.ps1
mvn package
.\tools\run-prd.ps1
```

1. `setup-prd-runtime.ps1` — скачивает [официальный архив PRD 3.9.1 GA](https://sourceforge.net/projects/jfreereport/files/04.%20Report%20Designer/3.9.1-stable/prd-ce-3.9.1-GA.zip/) и распаковывает в `runtime\prd-ce-3.9.1-GA\` (каталог в `.gitignore`).
2. `mvn package` — собирает `target\prd-ui-fork-3.9.1-fork-1.jar`, кладёт FlatLaf в `target\patch-lib\`.
3. `run-prd.ps1` — стартует `org.pentaho.reporting.designer.core.ReportDesigner` с нужным classpath и `--add-opens` для Java 9+.

Если скрипт не запускается из-за политики выполнения:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

или:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run-prd.ps1
```

## Что не попадает в Git

См. `.gitignore`: тяжёлый рантайм (`runtime/prd-ce-3.9.1-GA/`, zip) и артефакты сборки (`target/`).

## Лицензия

Исходный продукт Pentaho Report Designer распространяется под **GNU LGPL** (см. заголовки файлов и текст на сплэше приложения). Этот репозиторий содержит модификации поверх того же правового режима; при публикации сохраняйте совместимость с LGPL и указывайте upstream.
