package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import com.github.kqfall1.java.blackjackEngine.model.cards.Shoe;
import com.github.kqfall1.java.blackjackEngine.model.engine.BlackjackRulesetConfiguration;
import com.github.kqfall1.java.blackjackEngine.view.swingApplication.ui.UiConstants;
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
    private final Insets JCOMPONENT_INSETS = new Insets(UiConstants.MARGIN_EXTRA_EXTRA_SMALL, UiConstants.MARGIN_EXTRA_EXTRA_SMALL, UiConstants.MARGIN_EXTRA_EXTRA_SMALL, UiConstants.MARGIN_EXTRA_EXTRA_SMALL);
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
        final var mainPanelDimension = UiConstants.GAME_JDIALOG_DIMENSION;
        doublingDownOnSplitHandsAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL);
        doublingDownOnSplitHandsAllowed.setSelected(UiConstants.PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL, false));
        errorJLabel = new JLabel();
        errorJLabel.setMaximumSize(new Dimension(mainPanelDimension.width, errorJLabel.getHeight()));
        loggingEnabled = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL);
        loggingEnabled.setSelected(UiConstants.PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL, false));
        maximumSplitCountInput = new JSpinner(new SpinnerNumberModel(
            UiConstants.PREFERENCES_NODE.getInt(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL, 1),
            0, Integer.MAX_VALUE, 1
        ));
        maximumSplitCountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL);
        minimumBetAmountInput = new ValidatedJTextField();
        ((JTextField) minimumBetAmountInput).setText(Double.valueOf(UiConstants.PREFERENCES_NODE.getDouble(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL, 1)).toString());
        minimumBetAmountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL);
        playButton = new JButton(UiConstants.GAME_CONFIG_JDIALOG_PLAY_BUTTON_LABEL);
        playButton.addActionListener(this::readConfigValues);
        playerInitialChipsInput = new ValidatedJTextField();
        ((JTextField) playerInitialChipsInput).setText(Double.valueOf(UiConstants.PREFERENCES_NODE.getDouble(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL, 1)).toString());
        playerInitialChipsJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL);
        this.rootJFrame = rootJFrame;
        shoeDeckCountInput = new JSpinner(new SpinnerNumberModel(
            UiConstants.PREFERENCES_NODE.getInt(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL, Shoe.MINIMUM_NUMBER_OF_DECKS),
            Shoe.MINIMUM_NUMBER_OF_DECKS, Shoe.MAXIMUM_NUMBER_OF_DECKS, 1
        ));
        shoeDeckCountJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL);
        shoePenetrationInput = new ValidatedJTextField();
        ((JTextField) shoePenetrationInput).setText(Double.valueOf(UiConstants.PREFERENCES_NODE.getDouble(
            UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL, Shoe.MINIMUM_PENETRATION
        )).toString());
        shoePenetrationJLabel = new JLabel(UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL);
        shouldDealerHitOnSoft17 = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL);
        shouldDealerHitOnSoft17.setSelected(UiConstants.PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL, false));
        splittingAcesAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL);
        splittingAcesAllowed.setSelected(UiConstants.PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL, false));
        surrenderingAllowed = new JCheckBox(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL);
        surrenderingAllowed.setSelected(UiConstants.PREFERENCES_NODE.getBoolean(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL, false));

        final var jCheckBoxPanelWrapper = new JPanel();
        jCheckBoxPanelWrapper.setLayout(new BoxLayout(jCheckBoxPanelWrapper, BoxLayout.Y_AXIS));
        jCheckBoxPanelWrapper.add(doublingDownOnSplitHandsAllowed);
        jCheckBoxPanelWrapper.add(loggingEnabled);
        jCheckBoxPanelWrapper.add(shouldDealerHitOnSoft17);
        jCheckBoxPanelWrapper.add(splittingAcesAllowed);
        jCheckBoxPanelWrapper.add(surrenderingAllowed);
        jCheckBoxPanelWrapper.setMaximumSize(jCheckBoxPanelWrapper.getPreferredSize());

        final var jTextFieldPanelWrapper = new JPanel(new GridBagLayout());
        jTextFieldPanelWrapper.add(maximumSplitCountJLabel, getJLabelConstraints());
        jTextFieldPanelWrapper.add(maximumSplitCountInput, getSecondaryJComponentConstraints());
        jTextFieldPanelWrapper.add(minimumBetAmountJLabel, getJLabelConstraints());
        jTextFieldPanelWrapper.add((ValidatedJTextField) minimumBetAmountInput, getSecondaryJComponentConstraints());
        jTextFieldPanelWrapper.add(playerInitialChipsJLabel, getJLabelConstraints());
        jTextFieldPanelWrapper.add((ValidatedJTextField) playerInitialChipsInput, getSecondaryJComponentConstraints());
        jTextFieldPanelWrapper.add(shoeDeckCountJLabel, getJLabelConstraints());
        jTextFieldPanelWrapper.add(shoeDeckCountInput, getSecondaryJComponentConstraints());
        jTextFieldPanelWrapper.add(shoePenetrationJLabel, getJLabelConstraints());
        jTextFieldPanelWrapper.add((ValidatedJTextField) shoePenetrationInput, getSecondaryJComponentConstraints());
        jTextFieldPanelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, jTextFieldPanelWrapper.getPreferredSize().height));

        final var errorJLabelPanelWrapper = new JPanel(new GridBagLayout());
        errorJLabelPanelWrapper.add(errorJLabel);
        errorJLabelPanelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, errorJLabelPanelWrapper.getPreferredSize().height));

        final var playButtonPanelWrapper = new JPanel(new GridBagLayout());
        playButtonPanelWrapper.add(playButton);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(jCheckBoxPanelWrapper);
        add(Box.createVerticalStrut(UiConstants.MARGIN_SMALL));
        add(jTextFieldPanelWrapper);
        add(errorJLabelPanelWrapper);
        add(playButtonPanelWrapper);
    }

    private Component[] getErrorRelatedComponents()
    {
        final var allComponents = AwtUtils.getNestedComponents(Optional.empty(), this);
        final var errorRelatedComponents = new HashSet<Component>();

        for (final var component : allComponents)
        {
            if (component instanceof NumberInputter)
            {
                errorRelatedComponents.add(component);
            }
        }

        return errorRelatedComponents.toArray(new Component[0]);
    }

    private GridBagConstraints getJLabelConstraints()
    {
        final var jLabelConstraints = new GridBagConstraints();

        jLabelConstraints.anchor = GridBagConstraints.WEST;
        jLabelConstraints.fill = GridBagConstraints.NONE;
        jLabelConstraints.gridx = 0;
        jLabelConstraints.gridy = GridBagConstraints.RELATIVE;
        jLabelConstraints.gridwidth = 1;
        jLabelConstraints.gridheight = 1;
        jLabelConstraints.insets = JCOMPONENT_INSETS;
        jLabelConstraints.weightx = 0;
        jLabelConstraints.weighty = 0;

        return jLabelConstraints;
    }

    private GridBagConstraints getSecondaryJComponentConstraints()
    {
        final var secondaryJComponentConstraints = new GridBagConstraints();

        secondaryJComponentConstraints.fill = GridBagConstraints.HORIZONTAL;
        secondaryJComponentConstraints.gridx = 1;
        secondaryJComponentConstraints.gridy = GridBagConstraints.RELATIVE;
        secondaryJComponentConstraints.gridwidth = 1;
        secondaryJComponentConstraints.gridheight = 1;
        secondaryJComponentConstraints.insets = JCOMPONENT_INSETS;
        secondaryJComponentConstraints.weightx = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;
        secondaryJComponentConstraints.weighty = UiConstants.DEFAULT_GRID_BAG_LAYOUT_WEIGHT;

        return secondaryJComponentConstraints;
    }

    private void readConfigValues(ActionEvent e)
    {
        int maximumSplitCount;
        BigDecimal minimumBetAmount;
        BigDecimal playerInitialChips;
        int shoeDeckCount;
        double shoePenetration;

        try
        {
            maximumSplitCount = (int) maximumSplitCountInput.getValue();
            minimumBetAmount = BigDecimal.valueOf(minimumBetAmountInput.getNumber(Optional.empty(), 1, Float.MAX_VALUE).join());
            playerInitialChips = BigDecimal.valueOf(playerInitialChipsInput.getNumber(Optional.empty(), 1, Float.MAX_VALUE).join());
            shoeDeckCount = (int) shoeDeckCountInput.getValue();
            shoePenetration = shoePenetrationInput.getNumber(Optional.empty(), Shoe.MINIMUM_PENETRATION, Shoe.MAXIMUM_PENETRATION).join();
        }
        catch (CompletionException ex)
        {
            presentFailure(UiConstants.GAME_CONFIG_JDIALOG_FAILURE_LABEL, getErrorRelatedComponents());
            return;
        }

        UiConstants.PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED_LABEL, doublingDownOnSplitHandsAllowed.isSelected());
        UiConstants.PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_LOGGING_ENABLED_LABEL, loggingEnabled.isSelected());
        UiConstants.PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SHOULD_DEALER_HIT_ON_SOFT_17_LABEL, shouldDealerHitOnSoft17.isSelected());
        UiConstants.PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SPLITTING_ACES_ALLOWED_LABEL, splittingAcesAllowed.isSelected());
        UiConstants.PREFERENCES_NODE.putBoolean(UiConstants.GAME_CONFIG_JDIALOG_SURRENDERING_ALLOWED_LABEL, surrenderingAllowed.isSelected());
        UiConstants.PREFERENCES_NODE.putInt(UiConstants.GAME_CONFIG_JDIALOG_MAXIMUM_SPLIT_COUNT_LABEL, maximumSplitCount);
        UiConstants.PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_MINIMUM_BET_AMOUNT_LABEL, minimumBetAmount.doubleValue());
        UiConstants.PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_PLAYER_INITIAL_CHIPS_LABEL, playerInitialChips.doubleValue());
        UiConstants.PREFERENCES_NODE.putInt(UiConstants.GAME_CONFIG_JDIALOG_SHOE_DECK_COUNT_LABEL, shoeDeckCount);
        UiConstants.PREFERENCES_NODE.putDouble(UiConstants.GAME_CONFIG_JDIALOG_SHOE_PENETRATION_LABEL, shoePenetration);

        final var config = new BlackjackRulesetConfiguration();
        config.setDoublingDownOnSplitHandsAllowed(doublingDownOnSplitHandsAllowed.isSelected());
        config.setLoggingEnabled(loggingEnabled.isSelected());
        config.setShouldDealerHitOnSoft17(shouldDealerHitOnSoft17.isSelected());
        config.setSplittingAcesAllowed(splittingAcesAllowed.isSelected());
        config.setSurrenderingAllowed(surrenderingAllowed.isSelected());
        config.setMaximumSplitCount(maximumSplitCount);
        config.setMinimumBetAmount(minimumBetAmount);
        config.setPlayerInitialChips(playerInitialChips);
        config.setShoeDeckCount(shoeDeckCount);
        config.setShoePenetration(shoePenetration);

        final var window = SwingUtilities.getWindowAncestor(this);
        window.dispose();

        if (rootJFrame instanceof MainMenuJFrame mainMenuJFrame)
        {
            mainMenuJFrame.newGame(config);
        }
        else
        {
            new MainMenuJFrame().newGame(config);
            rootJFrame.dispose();
        }
    }

    @Override
    public void presentFailure(String message, Component... components)
    {
        final var defaultJTextFieldBorder = new JTextField().getBorder();
        errorJLabel.setText(message);

        for (final var component : components)
        {
            ((JComponent) component).setBorder(UiConstants.BORDER_RED);
        }

        SwingUtilities.invokeLater(() ->
        {
            new Timer(UiConstants.SLEEP_INTERVAL, event ->
            {
                for (final var component : components)
                {
                    final var JCOMPONENT = (JComponent) component;
                    JCOMPONENT.setBorder(defaultJTextFieldBorder);
                }

                errorJLabel.setText("");
                ((Timer) event.getSource()).stop();
            }).start();
        });
    }
}