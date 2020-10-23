package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_BLOCK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.ParserUtil.parseBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Block;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.RoomInBlockPredicate;
import seedu.address.storage.JsonAddressBookStorage;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    private static final Logger logger = LogsCenter.getLogger(JsonAddressBookStorage.class);
    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        requireNonNull(args);
        logger.log(Level.INFO, "going to start parsing find command");
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_BLOCK);

        List<Predicate<Person>> predicates = new ArrayList<>();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            predicates.add(getNameKeywordPredicate(argMultimap.getValue(PREFIX_NAME).get()));
        }

        if (argMultimap.getValue(PREFIX_BLOCK).isPresent()) {
            predicates.add(getBlockPredicate(argMultimap.getValue(PREFIX_BLOCK).get()));
        }

        if (predicates.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        return new FindCommand(predicates);
    }

    private Predicate<Person> getBlockPredicate(String block) throws ParseException {
        logger.log(Level.INFO, "getting block predicate");
        Block searchedBlock = parseBlock(block);
        return new RoomInBlockPredicate(searchedBlock);
    }

    private Predicate<Person> getNameKeywordPredicate(String keywords) throws ParseException {
        logger.log(Level.INFO, "adding name keywords to filter");
        String trimmedKeywords = keywords.trim();
        if (trimmedKeywords.isEmpty()) {
            logger.log(Level.WARNING, "empty keywords for name");
            throw new ParseException(FindCommand.MESSAGE_EMPTY_KEYWORD);
        }
        String[] nameKeywords = trimmedKeywords.split("\\s+");
        assert nameKeywords.length > 0 : "there should be some keywords";
        return new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
    }
}
