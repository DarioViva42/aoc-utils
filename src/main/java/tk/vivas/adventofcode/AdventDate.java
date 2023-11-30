package tk.vivas.adventofcode;

/**
 * A record holding year and day information used to solve specific puzzles.
 * @param year The year of the current Advent of Code event.
 * @param day The day that is worked on.
 */
record AdventDate(int year, int day) {
    /**
     * This method reads the year and day information from the directory where the {@code callerClass} is located.
     * @param callerClass The class where the utility method was called from.
     * @return A date containing the year and the day.
     */
    public static AdventDate fromClass(Class<?> callerClass) {
        String[] tokens = callerClass
                .getPackageName()
                .split("\\.");
        int day = Integer.parseInt(tokens[tokens.length - 1].substring(3));
        return new AdventDate(2022, day);
    }
}
