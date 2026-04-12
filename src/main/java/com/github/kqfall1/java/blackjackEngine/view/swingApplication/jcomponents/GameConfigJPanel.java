package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.UiConstants;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.jframes.MainMenuJFrame;
import com.github.kqfall1.java.frameworks.awt.AwtUtils;
import com.github.kqfall1.java.frameworks.awt.swing.ValidatedJTextField;
import com.github.kqfall1.java.interfaces.FailurePresenter;
import com.github.kqfall1.java.interfaces.inputters.NumberInputter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.concurrent.CompletionException;
import java.util.HashSet;
import java.util.prefs.Preferences;
import java.util.Optional;
import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required by a {@code MainMenuJFrame} to configure a new blackjack game.
 *
 * <p>Implements {@code FailurePresenter} to bolster application's user experience.</p>
 *
 * @author kqfall1
 * @since 14/02/2026
 */
public final class GameConfigJPanel extends JPanel implements FailurePresenter
{
    private final JCheckBox doublingDownOnSplitHandsAllowed;
    private final JLabel errorJLabel;
    private static final int JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE = 5;
    private static final Insets JTEXT_FIELD_PANEL_COMPONENT_INSETS = new Insets(
        JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE,
        JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE,
        JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE,
        JTEXT_FIELD_PANEL_COMPONENT_INSETS_VALUE
    );
    private final JCheckBox loggingEnabled;
    private final JSpinner maximumSplitCountInput;
    private final JLabel maximumSplitCountJLabel;
    private final NumberInputter minimumBetAmountInput;
    private final JLabel minimumBetAmountJLabel;
    private final JButton playButton;
    private final NumberInputter playerInitialChipsInput;
    private final JLabel playerInitialChipsJLabel;
    private final JFrame rootJFrame;
    private final JSpinner shoeDeckCountInput;
    private final JLabel shoeDeckCountJLabel;
    private final NumberInputter shoePenetrationInput;
    private final JLabel shoePenetrationJLabel;
    private final JCheckBox shouldDealerHitOnSoft17;
    private final JCheckBox splittingAcesAllowed;
    private final JCheckBox surrenderingAllowed;

    public GameConfigJPanel(JFrame rootJFrame)
    {
        final var MAIN_PANEL_DIMENSION = UiConstants.getGameConfigJDialogDimension();
        final var PREFERENCES_NODE = Preferences.userRoot().node(UiConstants.SWING_APPLICATION_PACKAGE_NAME);

        doublingDownOnSplitHandsAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL);
        doublingDownOnSplitHandsAllowed.setSelected(PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL, false));
        errorJLabel = new JLabel();
        errorJLabel.setMaximumSize(new Dimension(MAIN_PANEL_DIMENSION.width, errorJLabel.getHeight()));
        loggingEnabled = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL);
        loggingEnabled.setSelected(PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL, false));
        maximumSplitCountInput = new JSpinner(new SpinnerNumberModel(
            PREFERENCES_NODE.getInt(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL, 1),
            0, Integer.MAX_VALUE, 1
        ));
        maximumSplitCountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL);
        minimumBetAmountInput = new ValidatedJTextField();
        ((JTextField) minimumBetAmountInput).setText(Double.valueOf(PREFERENCES_NODE.getDouble(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL, 1)).toString());
        minimumBetAmountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL);
        playButton = new JButton(UiConstants.GAME_CONFIG_JDIALOG_PLAY_BUTTON_LABEL);
        playButton.addActionListener(this::readConfigValues);
        playerInitialChipsInput = new ValidatedJTextField();
        ((JTextField) playerInitialChipsInput).setText(Double.valueOf(PREFERENCES_NODE.getDouble(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL, 1)).toString());
        playerInitialChipsJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL);
        this.rootJFrame = rootJFrame;
        shoeDeckCountInput = new JSpinner(new SpinnerNumberModel(
            PREFERENCES_NODE.getInt(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL, Shoe.MINIMUM_NUMBER_OF_DECKS),
            Shoe.MINIMUM_NUMBER_OF_DECKS, Shoe.MAXIMUM_NUMBER_OF_DECKS, 1
        ));
        shoeDeckCountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL);
        shoePenetrationInput = new ValidatedJTextField();
        ((JTextField) shoePenetrationInput).setText(Double.valueOf(PREFERENCES_NODE.getDouble(
            UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL, Shoe.MINIMUM_PENETRATION
        )).toString());
        shoePenetrationJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL);
        shouldDealerHitOnSoft17 = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL);
        shouldDealerHitOnSoft17.setSelected(PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL, false));
        splittingAcesAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL);
        splittingAcesAllowed.setSelected(PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL, false));
        surrenderingAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL);
        surrenderingAllowed.setSelected(PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL, false));

        final var JCHECK_BOX_PANEL_WRAPPER = new JPanel();
        JCHECK_BOX_PANEL_WRAPPER.setLayout(new BoxLayout(JCHECK_BOX_PANEL_WRAPPER, BoxLayout.Y_AXIS));
        JCHECK_BOX_PANEL_WRAPPER.add(doublingDownOnSplitHandsAllowed);
        JCHECK_BOX_PANEL_WRAPPER.add(loggingEnabled);
        JCHECK_BOX_PANEL_WRAPPER.add(shouldDealerHitOnSoft17);
        JCHECK_BOX_PANEL_WRAPPER.add(splittingAcesAllowed);
        JCHECK_BOX_PANEL_WRAPPER.add(surrenderingAllowed);
        JCHECK_BOX_PANEL_WRAPPER.setMaximumSize(JCHECK_BOX_PANEL_WRAPPER.getPreferredSize());

        final var JTEXT_FIELD_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        JTEXT_FIELD_PANEL_WRAPPER.add(maximumSplitCountJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(maximumSplitCountInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(minimumBetAmountJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((ValidatedJTextField) minimumBetAmountInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(playerInitialChipsJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((ValidatedJTextField) playerInitialChipsInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoeDeckCountJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoeDeckCountInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add(shoePenetrationJLabel, getJLabelConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.add((ValidatedJTextField) shoePenetrationInput, getSecondaryJComponentConstraints());
        JTEXT_FIELD_PANEL_WRAPPER.setMaximumSize(new Dimension(Integer.MAX_VALUE, JTEXT_FIELD_PANEL_WRAPPER.getPreferredSize().height));

        final var ERROR_JLABEL_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        ERROR_JLABEL_PANEL_WRAPPER.add(errorJLabel);
        ERROR_JLABEL_PANEL_WRAPPER.setMaximumSize(new Dimension(Integer.MAX_VALUE, ERROR_JLABEL_PANEL_WRAPPER.getPreferredSize().height));

        final var PLAY_BUTTON_PANEL_WRAPPER = new JPanel(new GridBagLayout());
        PLAY_BUTTON_PANEL_WRAPPER.add(playButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(JCHECK_BOX_PANEL_WRAPPER);
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(JTEXT_FIELD_PANEL_WRAPPER);
        add(ERROR_JLABEL_PANEL_WRAPPER);
        add(PLAY_BUTTON_PANEL_WRAPPER);
    }

    private Component[] getErrorRelatedComponents()
    {
        final var ALL_COMPONENTS = AwtUtils.getNestedComponents(Optional.empty(), this);
        final var ERROR_RELATED_COMPONENTS = new HashSet<Component>();

        for (Component component : ALL_COMPONENTS)
        {
            if (component instanceof NumberInputter)
            {
                ERROR_RELATED_COMPONENTS.add(component);
            }
        }

        return ERROR_RELATED_COMPONENTS.toArray(new Component[0]);
    }

    private GridBagConstraints getJLabelConstraints()
    {
        final var JLABEL_CONSTRAINTS = new GridBagConstraints();

        JLABEL_CONSTRAINTS.anchor = GridBagConstraints.WEST;
        JLABEL_CONSTRAINTS.fill = GridBagConstraints.NONE;
        JLABEL_CONSTRAINTS.gridx = 0;
        JLABEL_CONSTRAINTS.gridy = GridBagConstraints.RELATIVE;
        JLABEL_CONSTRAINTS.gridwidth = 1;
        JLABEL_CONSTRAINTS.gridheight = 1;
        JLABEL_CONSTRAINTS.insets = JTEXT_FIELD_PANEL_COMPONENT_INSETS;
        JLABEL_CONSTRAINTS.weightx = 0;
        JLABEL_CONSTRAINTS.weighty = 0;

        return JLABEL_CONSTRAINTS;
    }

    private GridBagConstraints getSecondaryJComponentConstraints()
    {
        final var SECONDARY_JCOMPONENT_CONSTRAINTS = new GridBagConstraints();

        SECONDARY_JCOMPONENT_CONSTRAINTS.fill = GridBagConstraints.HORIZONTAL;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridx = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridy = GridBagConstraints.RELATIVE;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridwidth = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.gridheight = 1;
        SECONDARY_JCOMPONENT_CONSTRAINTS.insets = JTEXT_FIELD_PANEL_COMPONENT_INSETS;
        SECONDARY_JCOMPONENT_CONSTRAINTS.weightx = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;
        SECONDARY_JCOMPONENT_CONSTRAINTS.weighty = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;

        return SECONDARY_JCOMPONENT_CONSTRAINTS;
    }

    private void readConfigValues(ActionEvent e)
    {
        int maximumSplitCount;
        BigDecimal minimumBetAmount;
        BigDecimal playerInitialChips;
        int shoeDeckCount;
        double shoePenetration;
        final var PREFERENCES_NODE = Preferences.userRoot().node(UiConstants.SWING_APPLICATION_PACKAGE_NAME);

        try
        {
            maximumSplitCount = (int) maximumSplitCountInput.getValue();
            minimumBetAmount = BigDecimal.valueOf(minimumBetAmountInput.getNumber(null, 1, Float.MAX_VALUE).join());
            playerInitialChips = BigDecimal.valueOf(playerInitialChipsInput.getNumber(null, 1, Float.MAX_VALUE).join());
            shoeDeckCount = (int) shoeDeckCountInput.getValue();
            shoePenetration = shoePenetrationInput.getNumber(null, Shoe.MINIMUM_PENETRATION, Shoe.MAXIMUM_PENETRATION).join();
        }
        catch (CompletionException ex)
        {
            presentFailure(UiConstants.GAME_CONFIG_JDIALOG_FAILURE_LABEL, getErrorRelatedComponents());
            return;
        }

        PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL, doublingDownOnSplitHandsAllowed.isSelected());
        PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL, loggingEnabled.isSelected());
        PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL, shouldDealerHitOnSoft17.isSelected());
        PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL, splittingAcesAllowed.isSelected());
        PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL, surrenderingAllowed.isSelected());
        PREFERENCES_NODE.putInt(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL, maximumSplitCount);
        PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL, minimumBetAmount.doubleValue());
        PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL, playerInitialChips.doubleValue());
        PREFERENCES_NODE.putInt(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL, shoeDeckCount);
        PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL, shoePenetration);

        final var CONFIG = new BlackjackRulesetConfiguration();
        CONFIG.setDoublingDownOnSplitHandsAllowed(doublingDownOnSplitHandsAllowed.isSelected());
        CONFIG.setLoggingEnabled(loggingEnabled.isSelected());
        CONFIG.setShouldDealerHitOnSoft17(shouldDealerHitOnSoft17.isSelected());
        CONFIG.setSplittingAcesAllowed(splittingAcesAllowed.isSelected());
        CONFIG.setSurrenderingAllowed(surrenderingAllowed.isSelected());
        CONFIG.setMaximumSplitCount(maximumSplitCount);
        CONFIG.setMinimumBetAmount(minimumBetAmount);
        CONFIG.setPlayerInitialChips(playerInitialChips);
        CONFIG.setShoeDeckCount(shoeDeckCount);
        CONFIG.setShoePenetration(shoePenetration);

        final var WINDOW = SwingUtilities.getWindowAncestor(this);
        WINDOW.dispose();

        if (rootJFrame instanceof MainMenuJFrame mainMenuJFrame)
        {
            mainMenuJFrame.newGame(CONFIG);
        }
        else
        {
            rootJFrame.setVisible(false);
            new MainMenuJFrame().newGame(CONFIG);
        }
    }

    @Override
    public void presentFailure(String message, Component... components)
    {
        final var DEFAULT_JTEXT_FIELD_BORDER = new JTextField().getBorder();
        errorJLabel.setText(message);

        for (Component component : components)
        {
            final var JCOMPONENT = (JComponent) component;
            JCOMPONENT.setBorder(UiConstants.BORDER_RED);
        }

        SwingUtilities.invokeLater(() ->
        {
            new Timer(UiConstants.SLEEP_INTERVAL, event ->
            {
                for (Component component : components)
                {
                    final var JCOMPONENT = (JComponent) component;
                    JCOMPONENT.setBorder(DEFAULT_JTEXT_FIELD_BORDER);
                }

                errorJLabel.setText("");
                ((Timer) event.getSource()).stop();
            }).start();
        });
    }
}