//Quinn Keenan, 301504914, 02/09/2025

using BlackjackLibrary;

namespace WindowsFormsBlackjackApplication
{
    partial class BlackjackAppForm
    {
        private Panel activePanel; 
        private Panel ActivePanel
        {
            get
            {
                return activePanel;
            }
            set
            {
                activePanel = value;
            }
        }

        #region Windows Form Designer generated code
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(BlackjackAppForm));
            IntroPanel = new Panel();
            IntroBackgroundPictureBox = new PictureBox();
            IntroBlackjackLbl = new Label();
            IntroNewGameBtn = new Button();
            IntroQuitBtn = new Button();
            IntroBackgroundPictureLinkLbl = new LinkLabel();
            GameplayPanel = new Panel();
            GameplayBackgroundPictureBox = new PictureBox();
            GameplayPlayerSplitHandScoreLbl = new Label();
            GameplayDealerChipsLbl = new Label();
            GameplayPlayerChipsLbl = new Label();
            GameplayDealerScoreLbl = new Label();
            GameplayPlayerMainHandScoreLbl = new Label();
            GameplayNextBetOrHandBtn = new Button();
            GameplayBetAndYesOrNoBtn = new Button();
            GameplayBetAndYesOrNoTextBox = new TextBox();
            GameplayMessageAndPromptLbl = new Label();
            GameplayerSurrenderBtn = new Button();
            GameplayStandBtn = new Button();
            GameplaySplitBtn = new Button();
            GameplayHitBtn = new Button();
            GameplayDoubleDownBtn = new Button();
            GameplayDealerHandPanel = new FlowLayoutPanel();
            GameplayPlayerMainHandPanel = new FlowLayoutPanel();
            GameplayPlayerSplitHandPanel = new FlowLayoutPanel();
            IntroPanel.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)IntroBackgroundPictureBox).BeginInit();
            IntroBackgroundPictureBox.SuspendLayout();
            GameplayPanel.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)GameplayBackgroundPictureBox).BeginInit();
            GameplayBackgroundPictureBox.SuspendLayout();
            SuspendLayout();
            // 
            // IntroPanel
            // 
            IntroPanel.Controls.Add(IntroBackgroundPictureBox);
            IntroPanel.Location = new Point(0, 0);
            IntroPanel.Name = "IntroPanel";
            IntroPanel.Size = new Size(715, 425);
            IntroPanel.TabIndex = 0;
            // 
            // IntroBackgroundPictureBox
            // 
            IntroBackgroundPictureBox.BackgroundImage = (Image)resources.GetObject("IntroBackgroundPictureBox.BackgroundImage");
            IntroBackgroundPictureBox.Controls.Add(IntroBlackjackLbl);
            IntroBackgroundPictureBox.Controls.Add(IntroNewGameBtn);
            IntroBackgroundPictureBox.Controls.Add(IntroQuitBtn);
            IntroBackgroundPictureBox.Controls.Add(IntroBackgroundPictureLinkLbl);
            IntroBackgroundPictureBox.Location = new Point(0, 0);
            IntroBackgroundPictureBox.Name = "IntroBackgroundPictureBox";
            IntroBackgroundPictureBox.Size = new Size(715, 425);
            IntroBackgroundPictureBox.TabIndex = 0;
            IntroBackgroundPictureBox.TabStop = false;
            // 
            // IntroBlackjackLbl
            // 
            IntroBlackjackLbl.AutoSize = true;
            IntroBlackjackLbl.BackColor = Color.Transparent;
            IntroBlackjackLbl.Font = new Font("Georgia", 48F, FontStyle.Bold, GraphicsUnit.Point, 0);
            IntroBlackjackLbl.ForeColor = Color.Cornsilk;
            IntroBlackjackLbl.Location = new Point(354, 59);
            IntroBlackjackLbl.Name = "IntroBlackjackLbl";
            IntroBlackjackLbl.Size = new Size(346, 72);
            IntroBlackjackLbl.TabIndex = 1;
            IntroBlackjackLbl.Text = "Blackjack";
            // 
            // IntroNewGameBtn
            // 
            IntroNewGameBtn.BackColor = Color.Cornsilk;
            IntroNewGameBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            IntroNewGameBtn.Location = new Point(383, 157);
            IntroNewGameBtn.Name = "IntroNewGameBtn";
            IntroNewGameBtn.Size = new Size(125, 50);
            IntroNewGameBtn.TabIndex = 1;
            IntroNewGameBtn.Text = "New Game";
            IntroNewGameBtn.UseVisualStyleBackColor = false;
            IntroNewGameBtn.Click += IntroNewGameBtn_Click;
            // 
            // IntroQuitBtn
            // 
            IntroQuitBtn.BackColor = Color.Cornsilk;
            IntroQuitBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            IntroQuitBtn.Location = new Point(544, 157);
            IntroQuitBtn.Name = "IntroQuitBtn";
            IntroQuitBtn.Size = new Size(125, 50);
            IntroQuitBtn.TabIndex = 2;
            IntroQuitBtn.Text = "Quit";
            IntroQuitBtn.UseVisualStyleBackColor = false;
            // 
            // IntroBackgroundPictureLinkLbl
            // 
            IntroBackgroundPictureLinkLbl.ActiveLinkColor = Color.LightBlue;
            IntroBackgroundPictureLinkLbl.AutoSize = true;
            IntroBackgroundPictureLinkLbl.BackColor = Color.Transparent;
            IntroBackgroundPictureLinkLbl.LinkColor = Color.AliceBlue;
            IntroBackgroundPictureLinkLbl.Location = new Point(171, 397);
            IntroBackgroundPictureLinkLbl.Name = "IntroBackgroundPictureLinkLbl";
            IntroBackgroundPictureLinkLbl.Size = new Size(376, 15);
            IntroBackgroundPictureLinkLbl.TabIndex = 3;
            IntroBackgroundPictureLinkLbl.TabStop = true;
            IntroBackgroundPictureLinkLbl.Text = "\"Overhead view of casino chips stack on green poker table\" by Freepik";
            IntroBackgroundPictureLinkLbl.VisitedLinkColor = Color.LightBlue;
            IntroBackgroundPictureLinkLbl.LinkClicked += IntroBackgroundPictureLinkLbl_LinkClicked;
            // 
            // GameplayPanel
            // 
            GameplayPanel.Controls.Add(GameplayBackgroundPictureBox);
            GameplayPanel.Location = new Point(0, 0);
            GameplayPanel.Name = "GameplayPanel";
            GameplayPanel.Size = new Size(1000, 800);
            GameplayPanel.TabIndex = 0;
            GameplayPanel.Visible = false;
            // 
            // GameplayBackgroundPictureBox
            // 
            GameplayBackgroundPictureBox.BackgroundImage = (Image)resources.GetObject("GameplayBackgroundPictureBox.BackgroundImage");
            GameplayBackgroundPictureBox.Controls.Add(GameplayPlayerSplitHandScoreLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayDealerChipsLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayPlayerChipsLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayDealerScoreLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayPlayerMainHandScoreLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayNextBetOrHandBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayBetAndYesOrNoBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayBetAndYesOrNoTextBox);
            GameplayBackgroundPictureBox.Controls.Add(GameplayMessageAndPromptLbl);
            GameplayBackgroundPictureBox.Controls.Add(GameplayerSurrenderBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayStandBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplaySplitBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayHitBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayDoubleDownBtn);
            GameplayBackgroundPictureBox.Controls.Add(GameplayDealerHandPanel);
            GameplayBackgroundPictureBox.Controls.Add(GameplayPlayerMainHandPanel);
            GameplayBackgroundPictureBox.Controls.Add(GameplayPlayerSplitHandPanel);
            GameplayBackgroundPictureBox.Location = new Point(0, 0);
            GameplayBackgroundPictureBox.Name = "GameplayBackgroundPictureBox";
            GameplayBackgroundPictureBox.Size = new Size(1000, 800);
            GameplayBackgroundPictureBox.SizeMode = PictureBoxSizeMode.StretchImage;
            GameplayBackgroundPictureBox.TabIndex = 0;
            GameplayBackgroundPictureBox.TabStop = false;
            // 
            // GameplayPlayerSplitHandScoreLbl
            // 
            GameplayPlayerSplitHandScoreLbl.BackColor = Color.Transparent;
            GameplayPlayerSplitHandScoreLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Bold, GraphicsUnit.Point, 0);
            GameplayPlayerSplitHandScoreLbl.ForeColor = Color.Cornsilk;
            GameplayPlayerSplitHandScoreLbl.Location = new Point(817, 601);
            GameplayPlayerSplitHandScoreLbl.Name = "GameplayPlayerSplitHandScoreLbl";
            GameplayPlayerSplitHandScoreLbl.Size = new Size(153, 56);
            GameplayPlayerSplitHandScoreLbl.TabIndex = 11;
            GameplayPlayerSplitHandScoreLbl.Text = "Split Hand Score: ";
            GameplayPlayerSplitHandScoreLbl.TextAlign = ContentAlignment.MiddleLeft;
            // 
            // GameplayDealerChipsLbl
            // 
            GameplayDealerChipsLbl.BackColor = Color.Transparent;
            GameplayDealerChipsLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Bold, GraphicsUnit.Point, 0);
            GameplayDealerChipsLbl.ForeColor = Color.Cornsilk;
            GameplayDealerChipsLbl.Location = new Point(31, 59);
            GameplayDealerChipsLbl.Name = "GameplayDealerChipsLbl";
            GameplayDealerChipsLbl.Size = new Size(153, 72);
            GameplayDealerChipsLbl.TabIndex = 12;
            GameplayDealerChipsLbl.Text = "Dealer's Chips: ";
            GameplayDealerChipsLbl.TextAlign = ContentAlignment.MiddleLeft;
            // 
            // GameplayPlayerChipsLbl
            // 
            GameplayPlayerChipsLbl.BackColor = Color.Transparent;
            GameplayPlayerChipsLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Bold, GraphicsUnit.Point, 0);
            GameplayPlayerChipsLbl.ForeColor = Color.Cornsilk;
            GameplayPlayerChipsLbl.Location = new Point(817, 247);
            GameplayPlayerChipsLbl.Name = "GameplayPlayerChipsLbl";
            GameplayPlayerChipsLbl.Size = new Size(153, 77);
            GameplayPlayerChipsLbl.TabIndex = 11;
            GameplayPlayerChipsLbl.Text = "Player's Chips: ";
            GameplayPlayerChipsLbl.TextAlign = ContentAlignment.MiddleLeft;
            // 
            // GameplayDealerScoreLbl
            // 
            GameplayDealerScoreLbl.BackColor = Color.Transparent;
            GameplayDealerScoreLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Bold, GraphicsUnit.Point, 0);
            GameplayDealerScoreLbl.ForeColor = Color.Cornsilk;
            GameplayDealerScoreLbl.Location = new Point(31, 141);
            GameplayDealerScoreLbl.Name = "GameplayDealerScoreLbl";
            GameplayDealerScoreLbl.Size = new Size(153, 54);
            GameplayDealerScoreLbl.TabIndex = 11;
            GameplayDealerScoreLbl.Text = "Dealer's Score: ";
            GameplayDealerScoreLbl.TextAlign = ContentAlignment.MiddleLeft;
            // 
            // GameplayPlayerMainHandScoreLbl
            // 
            GameplayPlayerMainHandScoreLbl.BackColor = Color.Transparent;
            GameplayPlayerMainHandScoreLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Bold, GraphicsUnit.Point, 0);
            GameplayPlayerMainHandScoreLbl.ForeColor = Color.Cornsilk;
            GameplayPlayerMainHandScoreLbl.Location = new Point(817, 331);
            GameplayPlayerMainHandScoreLbl.Name = "GameplayPlayerMainHandScoreLbl";
            GameplayPlayerMainHandScoreLbl.Size = new Size(153, 54);
            GameplayPlayerMainHandScoreLbl.TabIndex = 10;
            GameplayPlayerMainHandScoreLbl.Text = "Main Hand Score: ";
            GameplayPlayerMainHandScoreLbl.TextAlign = ContentAlignment.MiddleLeft;
            // 
            // GameplayNextBetOrHandBtn
            // 
            GameplayNextBetOrHandBtn.BackColor = Color.Cornsilk;
            GameplayNextBetOrHandBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayNextBetOrHandBtn.Location = new Point(31, 331);
            GameplayNextBetOrHandBtn.Name = "GameplayNextBetOrHandBtn";
            GameplayNextBetOrHandBtn.Size = new Size(153, 44);
            GameplayNextBetOrHandBtn.TabIndex = 9;
            GameplayNextBetOrHandBtn.Text = "Next Bet/Hand";
            GameplayNextBetOrHandBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayBetAndYesOrNoBtn
            // 
            GameplayBetAndYesOrNoBtn.BackColor = Color.Cornsilk;
            GameplayBetAndYesOrNoBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayBetAndYesOrNoBtn.Location = new Point(817, 545);
            GameplayBetAndYesOrNoBtn.Name = "GameplayBetAndYesOrNoBtn";
            GameplayBetAndYesOrNoBtn.Size = new Size(153, 34);
            GameplayBetAndYesOrNoBtn.TabIndex = 8;
            GameplayBetAndYesOrNoBtn.Text = "Submit";
            GameplayBetAndYesOrNoBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayBetAndYesOrNoTextBox
            // 
            GameplayBetAndYesOrNoTextBox.Location = new Point(817, 516);
            GameplayBetAndYesOrNoTextBox.Name = "GameplayBetAndYesOrNoTextBox";
            GameplayBetAndYesOrNoTextBox.Size = new Size(153, 23);
            GameplayBetAndYesOrNoTextBox.TabIndex = 7;
            // 
            // GameplayMessageAndPromptLbl
            // 
            GameplayMessageAndPromptLbl.Font = new Font("Lucida Console", 15.75F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayMessageAndPromptLbl.Location = new Point(227, 210);
            GameplayMessageAndPromptLbl.Name = "GameplayMessageAndPromptLbl";
            GameplayMessageAndPromptLbl.Size = new Size(552, 175);
            GameplayMessageAndPromptLbl.TabIndex = 6;
            GameplayMessageAndPromptLbl.TextAlign = ContentAlignment.MiddleCenter;
            // 
            // GameplayerSurrenderBtn
            // 
            GameplayerSurrenderBtn.BackColor = Color.Cornsilk;
            GameplayerSurrenderBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayerSurrenderBtn.Location = new Point(817, 461);
            GameplayerSurrenderBtn.Name = "GameplayerSurrenderBtn";
            GameplayerSurrenderBtn.Size = new Size(153, 44);
            GameplayerSurrenderBtn.TabIndex = 5;
            GameplayerSurrenderBtn.Text = "Surrender";
            GameplayerSurrenderBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayStandBtn
            // 
            GameplayStandBtn.BackColor = Color.Cornsilk;
            GameplayStandBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayStandBtn.Location = new Point(817, 397);
            GameplayStandBtn.Name = "GameplayStandBtn";
            GameplayStandBtn.Size = new Size(153, 44);
            GameplayStandBtn.TabIndex = 4;
            GameplayStandBtn.Text = "Stand";
            GameplayStandBtn.UseVisualStyleBackColor = false;
            // 
            // GameplaySplitBtn
            // 
            GameplaySplitBtn.BackColor = Color.Cornsilk;
            GameplaySplitBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplaySplitBtn.Location = new Point(31, 526);
            GameplaySplitBtn.Name = "GameplaySplitBtn";
            GameplaySplitBtn.Size = new Size(153, 44);
            GameplaySplitBtn.TabIndex = 3;
            GameplaySplitBtn.Text = "Split";
            GameplaySplitBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayHitBtn
            // 
            GameplayHitBtn.BackColor = Color.Cornsilk;
            GameplayHitBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayHitBtn.Location = new Point(31, 461);
            GameplayHitBtn.Name = "GameplayHitBtn";
            GameplayHitBtn.Size = new Size(153, 44);
            GameplayHitBtn.TabIndex = 2;
            GameplayHitBtn.Text = "Hit";
            GameplayHitBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayDoubleDownBtn
            // 
            GameplayDoubleDownBtn.BackColor = Color.Cornsilk;
            GameplayDoubleDownBtn.Font = new Font("Constantia", 14.25F, FontStyle.Regular, GraphicsUnit.Point, 0);
            GameplayDoubleDownBtn.Location = new Point(31, 397);
            GameplayDoubleDownBtn.Name = "GameplayDoubleDownBtn";
            GameplayDoubleDownBtn.Size = new Size(153, 44);
            GameplayDoubleDownBtn.TabIndex = 1;
            GameplayDoubleDownBtn.Text = "Double Down";
            GameplayDoubleDownBtn.UseVisualStyleBackColor = false;
            // 
            // GameplayDealerHandPanel
            // 
            GameplayDealerHandPanel.BackColor = Color.Transparent;
            GameplayDealerHandPanel.Location = new Point(227, 22);
            GameplayDealerHandPanel.Name = "GameplayDealerHandPanel";
            GameplayDealerHandPanel.Size = new Size(544, 173);
            GameplayDealerHandPanel.TabIndex = 2;
            // 
            // GameplayPlayerMainHandPanel
            // 
            GameplayPlayerMainHandPanel.BackColor = Color.Transparent;
            GameplayPlayerMainHandPanel.Location = new Point(227, 397);
            GameplayPlayerMainHandPanel.Name = "GameplayPlayerMainHandPanel";
            GameplayPlayerMainHandPanel.Size = new Size(552, 173);
            GameplayPlayerMainHandPanel.TabIndex = 1;
            // 
            // GameplayPlayerSplitHandPanel
            // 
            GameplayPlayerSplitHandPanel.BackColor = Color.Transparent;
            GameplayPlayerSplitHandPanel.Location = new Point(227, 588);
            GameplayPlayerSplitHandPanel.Name = "GameplayPlayerSplitHandPanel";
            GameplayPlayerSplitHandPanel.Size = new Size(552, 173);
            GameplayPlayerSplitHandPanel.TabIndex = 2;
            // 
            // BlackjackAppForm
            // 
            AutoScaleDimensions = new SizeF(7F, 15F);
            AutoScaleMode = AutoScaleMode.Font;
            ClientSize = new Size(984, 786);
            Controls.Add(GameplayPanel);
            Controls.Add(IntroPanel);
            Name = "BlackjackAppForm";
            Text = "Blackjack";
            IntroPanel.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)IntroBackgroundPictureBox).EndInit();
            IntroBackgroundPictureBox.ResumeLayout(false);
            IntroBackgroundPictureBox.PerformLayout();
            GameplayPanel.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)GameplayBackgroundPictureBox).EndInit();
            GameplayBackgroundPictureBox.ResumeLayout(false);
            GameplayBackgroundPictureBox.PerformLayout();
            ResumeLayout(false);
        }

        private void SwitchPanels(Panel panel)
        {
            if (ActivePanel is not null)
            {
                ActivePanel.Visible = false;
            }

            ClientSize = panel.ClientSize;
            panel.Visible = true;
            ActivePanel = panel;
        }

        #endregion

        private Panel IntroPanel;
        private PictureBox IntroBackgroundPictureBox;
        private Label IntroBlackjackLbl;
        private Button IntroNewGameBtn;
        private Button IntroQuitBtn;
        private LinkLabel IntroBackgroundPictureLinkLbl;
        private Panel GameplayPanel;
        private PictureBox GameplayBackgroundPictureBox;
        private FlowLayoutPanel GameplayDealerHandPanel;
        private FlowLayoutPanel GameplayPlayerMainHandPanel;
        private Button GameplayDoubleDownBtn;
        private Button GameplayerSurrenderBtn;
        private Button GameplayStandBtn;
        private Button GameplaySplitBtn;
        private Button GameplayHitBtn;
        private Label GameplayMessageAndPromptLbl;
        private Button GameplayBetAndYesOrNoBtn;
        private TextBox GameplayBetAndYesOrNoTextBox;
        private Button GameplayNextBetOrHandBtn;
        private Label GameplayPlayerMainHandScoreLbl;
        private Label GameplayDealerScoreLbl;
        private Label GameplayDealerChipsLbl;
        private Label GameplayPlayerChipsLbl;
        private FlowLayoutPanel GameplayPlayerSplitHandPanel;
        private Label GameplayPlayerSplitHandScoreLbl;
    } 
}