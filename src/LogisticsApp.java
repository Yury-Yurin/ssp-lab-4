import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class LogisticsApp {

    private static final String NO_DATA = "Данные логистики еще не были загружены.";
    // ---------- XXX GLOBAL VARIABLES ------------

    private static final PrintStream stdout = System.out;
    private static final Scanner stdin = new Scanner(System.in);


    // -----------------------------------------
    private static final int NO_INPUT = -1;

    private final static String MAIN_MENU;
    private final static String SHOW_MENU;
    private static final String ADD_MENU;

    // ------------MAIN MENU -------------------
    private static final int EXIT = 0;
    private static final int SHOW_OPS = 1;
    private static final int ADD_OP = 2;
    private static final int SEARCH_OP = 3;

    private static final int MENU_RANGE = SEARCH_OP;

    // ----------- SHOW MENU --------------------
    private static final int BACK = 0;
    private static final int LOGISTIC_PLAN = 1;
    private static final int CODE_TABLE = 2;
    private static final int CTYS = 3;
    private static final int CARGOS = 4;
    private static final int ROUTE = 5;
    private static final int CARGO_TABLE_FOR_BUY = 6;
    private static final int CARGO_TABLE_FOR_SALE = 7;
    private static final int SHOW_RANGE = CARGO_TABLE_FOR_SALE;

    // ------------ ADD MENU --------------------
    private static final int NEW_CODE = 1;
    private static final int NEW_LOGISTIC_OP = 2;
    private static final int ADD_RANGE = NEW_LOGISTIC_OP;

    static {

        StringBuilder mmenu = new StringBuilder();
        mmenu.append(SHOW_OPS).append(" --- Показать...\n");
        mmenu.append(ADD_OP).append(" --- Добавить...\n");
        mmenu.append(SEARCH_OP).append(" --- Поиск...\n");
        mmenu.append(EXIT).append(" --- Выход");
        MAIN_MENU = mmenu.toString();

        StringBuilder smenu = new StringBuilder();
        smenu.append(LOGISTIC_PLAN).append(" --- ")
             .append("Текущий логистический план\n");
        smenu.append(CODE_TABLE).append(" --- ").append("Таблицу кодов\n");
        smenu.append(CTYS).append(" --- ").append("Множество городов\n");
        smenu.append(CARGOS).append(" --- ").append("Множество товаров\n");
        smenu.append(ROUTE).append(" --- ").append("Маршрут согласно плану\n");
        smenu.append(CARGO_TABLE_FOR_BUY).append(" --- ")
             .append("Таблица товаров на покупку\n");
        smenu.append(CARGO_TABLE_FOR_SALE).append(" --- ")
             .append("Таблица товаров на продажу\n");
        smenu.append(BACK).append(" --- ").append("Назад\n");

        SHOW_MENU = smenu.toString();

        StringBuilder amenu = new StringBuilder();
        amenu.append(NEW_CODE).append(" --- ")
             .append("Добавление нового товара\n");
        amenu.append(NEW_LOGISTIC_OP).append(" --- ")
             .append("Добавление новой логистики\n");
        amenu.append(BACK).append(" --- ").append("Назад\n");

        ADD_MENU = amenu.toString();
    }

    // ---------- XXX COLLECTIONS ------------
    // declare your collection objects here...
    static List<LogisticOp> logistics = new ArrayList<>();
    static Map<Integer, String> codes = new TreeMap<>();

    // ---------- XXX MAIN ------------
    public static void main(String[] args) {
        loadData();
        boolean exit = false;
        do {
            showMainMenu();
            int choice = userInput(MENU_RANGE);
            switch (choice) {
                case EXIT: // 0
                    exit = exit();
                    break;
                case SHOW_OPS: // 1
                    showMenu();
                    break;
                case ADD_OP: // 2
                    addMenu();
                    break;
                case SEARCH_OP:// 3
                    searchMenu();
                    break;
                default:
                    wrongChoice();
            }
            stdout.println();
        } while (!exit);
    }

    private static void wrongChoice() {
        stdout.println("Неверная опция: введено не число");
    }

    private static void showMainMenu() {
        stdout.println(MAIN_MENU);
    }

    private static boolean exit() {
        stdout.println("До свидания.");
        return true;
    }

    private static void loadData() {
        Loader.loadLogisticsData();
        Loader.loadCodeTable();
    }

    private static void showMenu() {
        ShowMenu.showTitle();

        int showOption = userInput(SHOW_RANGE);
        switch (showOption) {
            case BACK:
                return;
            case LOGISTIC_PLAN:
                ShowMenu.printCurrentLogistics();
                break;
            case CODE_TABLE:
                ShowMenu.printCodeTable();
                break;
            case CTYS:
                ShowMenu.pritnCTYSet();
                break;
            case CARGOS:
                ShowMenu.printCargoSet();
                break;
            case ROUTE:
                ShowMenu.printRoute();
                break;
            case CARGO_TABLE_FOR_BUY:
                ShowMenu.printBuyTable();
                break;
            case CARGO_TABLE_FOR_SALE:
                ShowMenu.printSaleTable();
                break;
        }
    }

    private static void addMenu() {
        AddMenu.showTitle();

        int addOption = userInput(ADD_RANGE);
        switch (addOption) {
            case BACK:
                return;
            case NEW_CODE:
                AddMenu.associateNewCode();
                break;
            case NEW_LOGISTIC_OP:
                AddMenu.addLogisticOperation();
                break;
        }
    }

    private static void searchMenu() {
        SearchMenu.searchByCode();
    }

    /**
     * Класс, содержащий методы для загрузки данных.
     */
    static class Loader {

        private static void loadLogisticsData() {
            Path path = Paths.get("./Logistics.csv");
            try (Scanner scanner = new Scanner(path, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    String record = scanner.nextLine();
                    LogisticOp newOp = addLogisticsRecord(record);
                    /*
                     * TODO Task 2 --- Формирование логистического плана В
                     * секции COLLECTIONS определена переменная logistics -- это
                     * динамический массив (список) с элементами типа LogisticOp
                     * Присвойте этой ссылке значение какой-либо реализации
                     * списков. Заполните созданный список объектами LogisticOp
                     * полученными в результате выполнения предыдущего задания.
                     * В результате выполнения задания, все стоки из файла
                     * Logistics1.csv должны быть преобразованы в объекты
                     * LogisticsOp, которые будут хранится в списке logistics.
                     */
                    logistics.add(newOp);
                    /*
                     * Если все прошло успешно, выведите на экран загруженный
                     * логистический план: 1 (Показать) -> 1 (Teкущий
                     * логистический план). Если все хорошо, то вы увидите
                     * красивую таблицу. См. метод
                     * ShowMenu.printCurrentLogistics();
                     */
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err
                        .println("Не найден файл 'Logistics.csv'. Остановка приложения.");
                System.exit(-1);
            }
        }

        private static LogisticOp addLogisticsRecord(String record) {
            /*
             * TODO Task 1 --- Разбор строки и создание объекта LogisticOp.
             * Разбейте строку с описакнием логистической операции на отдельные
             * элементы (подойдет метод slit()). Создайте объект LogisticOp,
             * передав все полученые значения в конструктор этого объекта.
             * Верните из метода полученный объект LogisticOp.
             */

            String[] splittedRecord = record.split(";");

            String CTY = splittedRecord[0];
            String CID = splittedRecord[1];
            String OP = splittedRecord[2];

            LogisticOp value = new LogisticOp(CTY, CID, OP);
            return value;
        }

        private static void loadCodeTable() {
            Path path = Paths.get("./Codes.csv");
            try (Scanner scanner = new Scanner(path, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    String record = scanner.nextLine();
                    /*
                     * TODO Task 3 --- Создайте и заполните таблицу кодов.
                     * Создайте глобальную карту, в котрую загрузите содержиое
                     * файла Codes.csv.
                     */
                    /*
                     * В данном методе файл Codes.csv читается построчно, где
                     * каждая строка записывается в переменную record. Строку
                     * необходимо разложить на составляющие и записать их в
                     * глобальную карту. Обратите внимание, что некоторые строки
                     * файла Codes.csv имеют ключ, но не имеют значение!
                     */

                    String[] splittedRecord = record.split(";");
                    String label = (splittedRecord.length > 1) ? splittedRecord[1]: "";
                    codes.put(Integer.valueOf(splittedRecord[0]), label);
                }
            } catch (IOException e) {
                System.err
                        .println("Не найден файл 'Codes.csv'. Остановка приложения.");
                System.exit(-1);
            }
        }
    }

    /**
     * Класс, содержащий методы для
     */
    static class ShowMenu {

        private static void printCurrentLogistics() {

            if (logistics == null) {
                System.out.println(NO_DATA);
                return;
            }

            int size = logistics.size();
            System.out.printf("%3s\t%8s\t%3s\t%-4s%n", "No", "CTY   ", "CID",
                              "OP");
            for (int index = 0; index < size; index++) {
                LogisticOp op = logistics.get(index);

                System.out.printf("%3d\t%8s\t%3s\t%-4s%n", index, op.CTY,
                                  op.CID, op.OP);
            }
        }

        private static void showTitle() {
            stdout.println(SHOW_MENU);
        }

        private static void printCodeTable() {
            /*
             * TODO Task 4 --- Выведите на экран содержимое карты в
             * отсортированном виде, хранящей коды товаров. Выполните итерацию
             * по карте как через множество записей (entrySet). Использовать
             * форматированный вывод. Формат вывода на экран должен иметь вид
             * (без нижних подчёркиваний):
             */
            // | Код товара | Товар _|
            // | _________1 | Бензин |
            System.out.printf("| Код товара | %17s  |\n", "Товар");
            System.out.println("-----------------------------------\n");

            codes.entrySet()
                    .stream()
                    .forEach(entry -> {
                        System.out.printf("| %10d | %17s  |\n", entry.getKey(), entry.getValue());
                    });

        }

        private static void pritnCTYSet() {
            /*
             * TODO Task 5 --- Вывести на экран множество всех городов,
             * присутствующих в логистическом плане.
             */

            logistics.stream()
                    .map(logisticOp -> logisticOp.CTY)
                    .collect(Collectors.toSet())
                        .stream()
                        .forEach(stdout::println);


        }

        private static void printCargoSet() {
            /*
             * TODO Task 6 --- Вывести множество всех товаров, присутствующих в
             * текущем логистическом плане
             */

            logistics.stream()
                    .map(logisticOp -> codes.get(Integer.valueOf(logisticOp.CID)))
                    .collect(Collectors.toSet())
                    .forEach(stdout::println);

        }

        private static void printRoute() {
            /*
             * TODO Task 7 - Создание маршрута на основе плана Напишите
             * алгоритм, который простматривает логистический план и создает
             * маршрут посещений городов. Создать маршрут -- это получить список
             * всех городов в логистике. Если агент выполняет несколько операций
             * в одном городе, то этот город считается один раз. Когда маршрут
             * создан выведите его содержимое в стандартный вывод.
             */

            for (int i = 0; i < logistics.size(); i++) {
                if(i > 0) {
                    if(logistics.get(i).getCTY().equals(logistics.get(i - 1).getCTY())) {
                        continue;
                    }
                }
                System.out.println(logistics.get(i).getCTY());
            }

        }

        private static void printBuyTable() {
            /*
             * TODO Task 8 --- вывести таблицу в которой перечисленно, в каких
             * городах можно купить ту или иную вещь. | Товар | Города |
             */
            stdout.printf("| %20s | %30s |\n\n", "Товар", "Города");

            logistics.stream()
                    .filter(logisticOp -> logisticOp.getOP().startsWith("+"))
                    .collect(Collectors.groupingBy(
                            LogisticOp::getCID,
                            Collectors.mapping(LogisticOp::getCTY, Collectors.toSet())
                    ))
                    .entrySet()
                        .stream()
                        .forEach(entry -> {
                            String cities = entry.getValue().stream().reduce("", (a, b) -> a + " " + b);
                            stdout.printf("| %20s | %30s |\n", codes.get(Integer.valueOf(entry.getKey())), cities);
                        });

        }

        private static void printSaleTable() {
            /*
             * TODO Task 9 --- вывести таблицу в которой перечисленно, в каких
             * городах можно продать ту или иную вещь. | Товар | Города |
             */

            stdout.printf("| %20s | %30s |\n\n", "Товар", "Города");

            logistics.stream()
                    .filter(logisticOp -> logisticOp.getOP().startsWith("-"))
                    .collect(Collectors.groupingBy(
                            LogisticOp::getCID,
                            Collectors.mapping(LogisticOp::getCTY, Collectors.toSet())
                    ))
                    .entrySet()
                    .stream()
                    .forEach(entry -> {
                        String cities = entry.getValue().stream().reduce("", (a, b) -> a + " " + b);
                        stdout.printf("| %20s | %30s |\n", codes.get(Integer.valueOf(entry.getKey())), cities);
                    });
        }
    }

    static class AddMenu {

        private static void associateNewCode() {
            /*
             * TODO Task 10 --- реализовать ввод данных для добавления в таблицу
             * нового кода. Если при добавлении кода такое значение кода уже
             * занято, то вывести сообщение о том, что вставка такого кода
             * невозможна.
             */

            stdout.print("Введите код товара: \n");
            Integer newCode = stdin.nextInt();
            String codeValue = codes.get( newCode);

            if(codeValue == null || codeValue.equals("")) {
                stdout.print("Введите наименование товара: \n");
                String newProduct = stdin.next();
                codes.put(newCode, newProduct);
            } else {
                stdout.println("Указанный код уже существует\n\n");
            }
        }

        private static void showTitle() {
            stdout.println(ADD_MENU);
        }

        private static void addLogisticOperation() {
            /*
             * TODO Тask 11 --- реализовать ввод данных для добавления новой
             * логистической операции. Пользователь должен вводить информацию о
             * CTY, CID, OP. На основе введенных данных создать новый объект
             * LogisticsOp и дописать его в список с операциями.
             */


            stdout.print("Введите город назначения: ");
            String CTY = stdin.next();

            stdout.print("Введите код товара: ");
            String CID = stdin.next();

            stdout.print("Введите операцию: ");
            String OP = stdin.next();

            LogisticOp logisticOp = new LogisticOp(CTY, CID, OP);
            logistics.add(logisticOp);

        }
    }

    static class SearchMenu {

        private static void searchByCode() {
            /*
             * TODO Task 12 --- Выполнить поиск всех записей в плане содержащих
             * определенный код товара.
             */
            stdout.print("Введите код товара: ");
            String code = stdin.next();
            stdout.printf("%15s\t%3s\t%5s\n", "CTY", "CID", "OP");
            logistics.stream()
                    .filter(logisticOp -> logisticOp.getCID().equals(code))
                    .forEach(logisticOp -> stdout.printf("%15s\t%3s\t%5s\n", logisticOp.getCTY(), logisticOp.getCID(), logisticOp.getOP()));
        }
    }

    private static int userInput(int maxRange) {
        String userLine = format("@user:(Введите выбор 0..%d)>> ", maxRange);
        int option = NO_INPUT;

        // Обработка ввода пользователя
        do {
            stdout.print(userLine);
            boolean isNumber = stdin.hasNextInt();
            if (!isNumber) {
                stdin.next(); // skip wrong input
                wrongChoice();
                continue;
            }

            option = stdin.nextInt();

            if (option < 0 || option > maxRange) {
                stdout.println("Неверная опция: " + option);
                option = NO_INPUT; // reset to no input
            }
        } while (option == NO_INPUT);
        return option;
    }

    /**
     * Объект, отображающий запись о логистической операции.
     */
    final static class LogisticOp {
        final String CTY;
        final String CID;
        final String OP;

        public LogisticOp(String cTY, String cID, String oP) {
            CTY = cTY;
            CID = cID;
            OP = oP;
        }

        public String getCTY() {
            return CTY;
        }

        public String getCID() {
            return CID;
        }

        public String getOP() {
            return OP;
        }

        @Override
        public String toString() {
            return String.format("[CTY=%s, CID=%s, OP=%s]", CTY, CID, OP);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((CID == null) ? 0 : CID.hashCode());
            result = prime * result + ((CTY == null) ? 0 : CTY.hashCode());
            result = prime * result + ((OP == null) ? 0 : OP.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            LogisticOp that = (LogisticOp) obj;
            return Objects.equals(this.CTY, that.CTY) //
                   && Objects.equals(this.CID, that.CID) //
                   && Objects.equals(this.OP, that.OP);
        }
    }
}