# Pentaho Report Designer 3.9.1 — UI fork

Форк интерфейса **Pentaho Report Designer (PRD) CE 3.9.1 GA**: экран загрузки, фон рабочей области, приветственное окно, настройки темы.

## Что внутри

- `prd-source-3.9.1-GA/` — исходники PRD (и патчи в этом форке).
- `pre-classic-source-3.9.1-GA/` — связанные исходники движка (импорт из upstream).
- `pom.xml` — сборка **overlay JAR** с переопределёнными классами UI и копированием `messages*.properties`, плюс **ZIP-дистрибутив** для переноса.
- `src/assembly/` — дескриптор сборки архива и скрипты `report-designer.bat` / `report-designer.ps1`.
- `tools/setup-prd-runtime.ps1` — загрузка и распаковка официального `prd-ce-3.9.1-GA.zip` в `runtime/` (не коммитится).
- `tools/run-prd.ps1` — тот же запуск, что и `PRD.bat`, но из PowerShell.
- **`PRD.bat`** — в корне проекта: двойной щелчок = запуск (после `setup-prd-runtime` и `mvn package`). Сообщения в bat на английском, чтобы `cmd.exe` не ломал файл из‑за кодировки.
- После `mvn package` — **портативный архив** `target/prd-ui-fork-3.9.1-fork-1-distribution.zip`: распаковать и запустить `report-designer.bat` или `report-designer.ps1` (рядом папки `lib/` и `patch/`). Один «fat» JAR из всех библиотек PRD не собираем — конфликты `META-INF` и подписей; ZIP — надёжный вариант для переноса.

## Требования

- **JDK** 8+ (проверено с JDK 21), `JAVA_HOME` желательно задать явно.
- **Apache Maven** 3.6+.

## Быстрый старт

**Запуск без PowerShell (для себя):** откройте в проводнике папку с проектом и дважды щёлкните **`PRD.bat`**. Откроется только Report Designer (чёрное окно консоли сразу закроется). Перед первым запуском один раз нужны `tools\setup-prd-runtime.ps1` и `mvn package` (как ниже). Чтобы не лазить каждый раз в папку — правый щелчок по `PRD.bat` → «Отправить» → «Рабочий стол (создать ярлык)».

Из корня репозитория в PowerShell (если удобнее так):

```powershell
.\tools\setup-prd-runtime.ps1
mvn package
.\tools\run-prd.ps1
```

1. `setup-prd-runtime.ps1` — скачивает [официальный архив PRD 3.9.1 GA](https://sourceforge.net/projects/jfreereport/files/04.%20Report%20Designer/3.9.1-stable/prd-ce-3.9.1-GA.zip/) и распаковывает в `runtime\prd-ce-3.9.1-GA\` (каталог в `.gitignore`).
2. `mvn package` — собирает `target\prd-ui-fork-3.9.1-fork-1.jar`, кладёт FlatLaf в `target\patch-lib\`, собирает **`target\prd-ui-fork-3.9.1-fork-1-distribution.zip`** (полный набор `lib` + патч + скрипты запуска).
3. `run-prd.ps1` — стартует `org.pentaho.reporting.designer.core.ReportDesigner` с нужным classpath и `--add-opens` для Java 9+.

### Запуск из ZIP (если перенесли только архив с другого компьютера)

Распакуйте `prd-ui-fork-*-distribution.zip`, откройте каталог `report-designer-fork-*` и выполните `report-designer.bat` или `.\report-designer.ps1`. Нужна только установленная JRE/JDK и переменная `JAVA_HOME` (желательно).

Если скрипт не запускается из-за политики выполнения:

```powershell
Set-ExecutionPolicy -Scope CurrentUser RemoteSigned
```

или:

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\run-prd.ps1
```

## Интерфейс
<img width="1280" height="684" alt="image" src="https://github.com/user-attachments/assets/b94ae60e-00e5-40c0-bc7c-e896631e8ae6" />

<img width="1913" height="1024" alt="image" src="https://github.com/user-attachments/assets/6d058ff7-60ab-42ef-a90c-7065fca01ac6" />

