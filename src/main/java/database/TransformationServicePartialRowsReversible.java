package database;


/**
 * Must EITHER return NULL, or do a reversible transformation.
 * Eg. UnwrapDoubleQuotes maps "quoted" to quoted, and maps unquoted to NULL.
 *
 * Useful when a transformation is sometimes fully reversible, but other times completely fails.
 * eg. CurrencySymbol maps "€" to "EUR", but fails to map "Potato" to anything (so returns NULL). The "EUR" is reversible to "€", but NULL is not reversible to "Potato".
 */
public abstract class TransformationServicePartialRowsReversible extends TransformationServiceReversible
{
}
