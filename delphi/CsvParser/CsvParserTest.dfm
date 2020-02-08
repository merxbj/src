object Form1: TForm1
  Left = 361
  Top = 323
  Width = 781
  Height = 415
  Caption = 'Form1'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  OldCreateOrder = False
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 16
  object Edit1: TEdit
    Left = 8
    Top = 16
    Width = 121
    Height = 24
    TabOrder = 0
    Text = 'C:\Users\JM185267\Desktop\jfric\kvf.csv'
  end
  object Button1: TButton
    Left = 136
    Top = 16
    Width = 75
    Height = 25
    Caption = 'Parse'
    TabOrder = 1
    OnClick = Button1Click
  end
  object ListBox1: TListBox
    Left = 8
    Top = 56
    Width = 241
    Height = 265
    ItemHeight = 16
    TabOrder = 2
  end
  object ListBox2: TListBox
    Left = 256
    Top = 56
    Width = 257
    Height = 265
    ItemHeight = 16
    TabOrder = 3
  end
  object ListBox3: TListBox
    Left = 520
    Top = 56
    Width = 233
    Height = 265
    ItemHeight = 16
    TabOrder = 4
  end
  object Button2: TButton
    Left = 8
    Top = 336
    Width = 75
    Height = 25
    Caption = '<<<'
    TabOrder = 5
    OnClick = Button2Click
  end
  object Button3: TButton
    Left = 672
    Top = 336
    Width = 75
    Height = 25
    Caption = '>>>'
    TabOrder = 6
    OnClick = Button3Click
  end
end
