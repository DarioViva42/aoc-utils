package vivas.tk.adventofcode;

record AdventDate(int year, int day) {
    public static AdventDate fromClass(Class<?> callerClass) {
        String[] tokens = callerClass
                .getPackageName()
                .split("\\.");
        int day = Integer.parseInt(tokens[tokens.length - 1].substring(3));
        return new AdventDate(2022, day);
    }
}
