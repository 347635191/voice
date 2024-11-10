package 实体类;

import java.util.Objects;

public class Word {
    private String keyword;
    private String replacement;

    public Word() {
    }

    public Word(String keyword, String replacement) {
        this.keyword = keyword;
        this.replacement = replacement;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(keyword, word.keyword) && Objects.equals(replacement, word.replacement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyword, replacement);
    }

    @Override
    public String toString() {
        return keyword + " " + replacement;
    }
}
