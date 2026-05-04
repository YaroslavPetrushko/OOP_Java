package ua.edu.sumdu.model;

import java.util.UUID;

/**
 * Контракт для об'єктів, що мають стабільний унікальний ідентифікатор.
 *
 * <p>Реалізується класом {@link Book} та, відповідно, всіма його нащадками.</p>
 */
public interface Identifiable {

    /**
     * Повертає унікальний ідентифікатор об'єкта.
     *
     * @return {@link UUID} цього об'єкта;ніколи не {@code null}
     */
    UUID getUuid();
}