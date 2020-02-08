object Form1: TForm1
  Left = 324
  Top = 12
  Width = 1007
  Height = 647
  Caption = 
    'P'#345'evodov'#253' m'#367'stek          AB-AP s.r.o       Petr Gronsky    2010' +
    '     v.1.0'
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  Menu = MainMenu1
  OldCreateOrder = False
  OnCreate = FormCreate
  PixelsPerInch = 96
  TextHeight = 13
  object Label1: TLabel
    Left = 120
    Top = 8
    Width = 32
    Height = 13
    Caption = 'Label1'
  end
  object ListBox1: TListBox
    Left = 208
    Top = 48
    Width = 361
    Height = 542
    ItemHeight = 13
    TabOrder = 0
  end
  object ListBox2: TListBox
    Left = 8
    Top = 40
    Width = 185
    Height = 542
    ItemHeight = 13
    TabOrder = 1
  end
  object ListBox3: TListBox
    Left = 592
    Top = 40
    Width = 361
    Height = 537
    ItemHeight = 13
    TabOrder = 2
  end
  object Edit1: TEdit
    Left = 232
    Top = 8
    Width = 121
    Height = 21
    TabOrder = 3
    Text = 'Edit1'
  end
  object MainMenu1: TMainMenu
    object Soubor1: TMenuItem
      Caption = 'Soubor'
      object Nati1: TMenuItem
        Caption = 'Na'#269'ti DATA'
        OnClick = Nati1Click
      end
      object VytvoskladMenu1: TMenuItem
        Caption = 'Vytvo'#345' skladov'#233' Menu'
        OnClick = VytvoskladMenu1Click
      end
      object Pidej1: TMenuItem
        Caption = 'P'#345'idej skladov'#233' karty'
        OnClick = Pidej1Click
      end
      object Vytvoceny1: TMenuItem
        Caption = 'Vytvo'#345' ceny'
        OnClick = Vytvoceny1Click
      end
      object N1: TMenuItem
        Caption = 'Na'#269'ti DATA pro Inventuru'
        OnClick = N1Click
      end
      object VytvorInventuru1: TMenuItem
        Caption = 'Vytvor Inventuru'
        OnClick = VytvorInventuru1Click
      end
      object Konec1: TMenuItem
        Caption = 'Konec'
        OnClick = Konec1Click
      end
      object Prepismnozstvi1: TMenuItem
        Caption = 'Prepis mnozstvi'
        OnClick = Prepismnozstvi1Click
      end
      object Naplndodavatelskycenik1: TMenuItem
        Caption = 'Napln dodavatelsky cenik'
        OnClick = Naplndodavatelskycenik1Click
      end
    end
    object Help1: TMenuItem
      Caption = 'Help'
      object patnetina1: TMenuItem
        Caption = #352'patn'#225' '#269'e'#353'tina'
        OnClick = patnetina1Click
      end
      object Log1: TMenuItem
        Caption = 'Log'
        OnClick = Log1Click
      end
      object DUle1: TMenuItem
        Caption = 'Dulezite'
        OnClick = DUle1Click
      end
    end
  end
  object OpenDialog1: TOpenDialog
    Left = 48
  end
  object SaveDialog1: TSaveDialog
    Left = 80
  end
end
