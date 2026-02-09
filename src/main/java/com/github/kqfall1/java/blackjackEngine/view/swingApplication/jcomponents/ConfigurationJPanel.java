package com.github.kqfall1.java.blackjackEngine.view.swingApplication.jcomponents;

import javax.swing.*;

/**
 * Contains all {@code JComponent} objects required by a {@code NewGameJFrame}.
 *
 * @author kqfall1
 * @since 08/02/2026
 */
public final class ConfigurationJPanel extends JPanel
{
    private final JCheckBox DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED;
    private final JCheckBox LOGGING_ENABLED;
    private final JCheckBox SHOULD_DEALER_HIT_ON_SOFT_17;
    private final JCheckBox SPLITTING_ACES_ALLOWED;
    private final JCheckBox SURRENDERING_ALLOWED;
    private final JCheckBox SURRENDERING_ON_SPLIT_HANDS_ALLOWED;

    public ConfigurationJPanel()
    {
        DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED = new JCheckBox("Doubling down on split hands allowed?");
        LOGGING_ENABLED = new JCheckBox("Is logging enabled?");
        SHOULD_DEALER_HIT_ON_SOFT_17 = new JCheckBox("Should the dealer hit on a soft 17?");
        SPLITTING_ACES_ALLOWED = new JCheckBox("Is splitting aces allowed?");
        SURRENDERING_ALLOWED = new JCheckBox("Is surrendering allowed?");
        SURRENDERING_ON_SPLIT_HANDS_ALLOWED = new JCheckBox("Is surrendering on split hands allowed?");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //setOpaque(false);
        add(DOUBLING_DOWN_ON_SPLIT_HANDS_ALLOWED);
        add(LOGGING_ENABLED);
        add(SHOULD_DEALER_HIT_ON_SOFT_17);
        add(SPLITTING_ACES_ALLOWED);
        add(SURRENDERING_ALLOWED);
    }
}