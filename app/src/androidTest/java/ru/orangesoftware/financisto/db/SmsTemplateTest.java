package ru.orangesoftware.financisto.db;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;
import ru.orangesoftware.financisto.model.SmsTemplate;
import ru.orangesoftware.financisto.test.SmsTemplateBuilder;

public class SmsTemplateTest extends AbstractDbTest {

    private SmsTemplate template777;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        String template = "*{{a}}. Summa {{P}} RUB. NOVYY PROEKT, MOSCOW. {{D}}. Dostupno {{b}}";
        template777 = SmsTemplateBuilder.withDb(db).title("777").accountId(7).categoryId(8).template(template).create();
    }

    public void test_duplication() throws Exception {
        long dupId = db.duplicate(SmsTemplate.class, template777.id);
        SmsTemplate dup = db.load(SmsTemplate.class, dupId);
        assertNotNull(dup);
        assertEquals(template777.template, dup.template);
        assertEquals(template777.title, dup.title);
        assertEquals(template777.accountId, dup.accountId);
        assertEquals(template777.categoryId, dup.categoryId);
        assertFalse(template777.id == dup.id);
    }

    public void test_sorting() throws Exception {
        String template1 = "*{{a}}. Summa {{p}} RUB. {{*}}, MOSCOW. {{d}}. Dostupno {{b}}";
        String template2 = "*{{a}}. Summa {{p}} RUB. NOVYY PROEKT, MOSCOW. {{d}}. Dostupno {{b}}";
        String template3 = "*{{a}}. Summa {{p}} RUB. NOVYY PROEKT, MOSCOW. {{d}}. Dostupno {{b}}";

        SmsTemplateBuilder.withDb(db).title("888").accountId(3).categoryId(8).template(template1).sortOrder(4).create();
        SmsTemplateBuilder.withDb(db).title("Tinkoff").accountId(6).categoryId(8).template(template1).sortOrder(7).create();
        SmsTemplateBuilder.withDb(db).title("888").accountId(1).categoryId(88).template(template2).sortOrder(2).create();
        SmsTemplateBuilder.withDb(db).title("Tinkoff").accountId(4).categoryId(88).template(template2).sortOrder(4).create();
        SmsTemplateBuilder.withDb(db).title("888").accountId(2).categoryId(89).template(template3).sortOrder(3).create();
        SmsTemplateBuilder.withDb(db).title("Tinkoff").accountId(5).categoryId(89).template(template3).sortOrder(6).create();

        try (Cursor c = db.getSmsTemplatesWithFullInfo()) {
            List<SmsTemplate> res = new ArrayList<>(c.getCount());
            while (c.moveToNext()) {
                SmsTemplate a = SmsTemplate.fromCursor(c);
                res.add(a);
            }

            assertEquals(7, res.get(0).accountId);
            assertEquals(1, res.get(1).accountId);
            assertEquals(2, res.get(2).accountId);
            assertEquals(3, res.get(3).accountId);
            assertEquals(4, res.get(4).accountId);
            assertEquals(5, res.get(5).accountId);
            assertEquals(6, res.get(6).accountId);
        }

        List<SmsTemplate> res = db.getSmsTemplatesByNumber("888");

        assertEquals("Number Query Sort Order mismatch: ", 1, res.get(0).accountId);
        assertEquals("Number Query Sort Order mismatch: ", 2, res.get(1).accountId);
        assertEquals("Number Query Sort Order mismatch: ", 3, res.get(2).accountId);
    }

    public void test_changing_sorting() throws Exception {
        SmsTemplate t1 = SmsTemplateBuilder.withDb(db).title("1").accountId(1).categoryId(8).template("first").create();
        SmsTemplate t2 = SmsTemplateBuilder.withDb(db).title("2").accountId(2).categoryId(8).template("second").create();
        SmsTemplate t3 = SmsTemplateBuilder.withDb(db).title("3").accountId(3).categoryId(8).template("third").create();
        SmsTemplate t4 = SmsTemplateBuilder.withDb(db).title("4").accountId(4).categoryId(8).template("4th").create();
        SmsTemplate t5 = SmsTemplateBuilder.withDb(db).title("5").accountId(5).categoryId(8).template("5th").create();
        SmsTemplate t6 = SmsTemplateBuilder.withDb(db).title("6").accountId(6).categoryId(8).template("6th").create();
        SmsTemplate t7 = SmsTemplateBuilder.withDb(db).title("7").accountId(7).categoryId(8).template("7th").create();

        assertEquals(2, t1.getSortOrder());
        assertEquals(3, t2.getSortOrder());
        assertEquals(4, t3.getSortOrder());
        assertEquals(5, t4.getSortOrder());
        assertEquals(6, t5.getSortOrder());
        assertEquals(7, t6.getSortOrder());
        assertEquals(8, t7.getSortOrder());
    }
}
