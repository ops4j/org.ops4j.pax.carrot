/*
 * $Id: JavaTemplates.xml 53870 2013-02-12 10:32:44Z tlangfeld $
 */
package org.ops4j.pax.carrot.sample1;

import java.util.Arrays;
import java.util.List;

import org.ops4j.pax.carrot.annotation.CollectionProvider;

public class WhitespaceText {

    public static class WhitespaceRow {

        private int id;

        private String text;


        public WhitespaceRow(int id, String text) {
            this.id = id;
            this.text = text;
        }


        public int getId() {
            return id;
        }


        public void setId(int id) {
            this.id = id;
        }


        public String getText() {
            return text;
        }


        public void setText(String text) {
            this.text = text;
        }


    }

    @CollectionProvider
    public List<WhitespaceRow> getRows() {
        return Arrays.asList(
                new WhitespaceRow(1, "none"), 
                new WhitespaceRow(2, "  leading spaces"), 
                new WhitespaceRow(3, "trailing spaces  "),
                new WhitespaceRow(4, "\u00A0leading non-breaking space"), 
                new WhitespaceRow(5, "trailing non-breaking space\u00A0"),
                new WhitespaceRow(6, "[  leading spaces]"), 
                new WhitespaceRow(7, "[trailing spaces  ]"),
                new WhitespaceRow(8, "[\u00A0leading non-breaking space]"), 
                new WhitespaceRow(9, "[trailing non-breaking space\u00A0]")
                );
    }

}
