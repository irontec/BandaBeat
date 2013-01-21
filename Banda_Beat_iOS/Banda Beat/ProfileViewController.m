//
//  ProfileViewController.m
//  Banden Lehia
//
//  Created by Iker Mendilibar on 24/10/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <CoreData/CoreData.h>

#import "ProfileViewController.h"
#import "ButtonViewCell.h"
#import "BLAPI.h"
#import "AppDelegate.h"
#import "HTMLViewController.h"


@interface ProfileViewController ()
@property(strong,nonatomic) BLAPI *api;
@property(strong,nonatomic) AppDelegate *appDelegate;
@property(strong,nonatomic) NSManagedObjectContext *context;
@end

@implementation ProfileViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.trackedViewName = @"Profile";
        
    [self.view setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];
    [self.tableView setBackgroundColor:[UIColor colorWithPatternImage:[UIImage imageNamed:@"background"]]];
    
    _api = [BLAPI sharedInstance];
    _appDelegate = (AppDelegate*) [[UIApplication sharedApplication] delegate];
    _context = [_appDelegate managedObjectContext];
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)logout:(id)sender
{
    [_appDelegate.player pause];
    BLAPI *api = [BLAPI sharedInstance];
    [self.navigationController removeFromParentViewController];
    [api logout];
}

-(IBAction)about:(id)sender
{
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"MainStoryboard" bundle:nil];
    HTMLViewController *vc = [sb instantiateViewControllerWithIdentifier:@"HTMLViewController"];
    [vc setFileName:@"about"];
    [self presentModalViewController:vc animated:YES];
}

-(IBAction)createPlaylist:(id)sender
{
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"http://bandabeat.com/#!/bandak"]];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 2;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
        
    UITableViewCell *cell;
    
    if (indexPath.row == 0) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"UsuarioCell"];
        cell.detailTextLabel.text = _api.username;
        
    } else if (indexPath.row == 1) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"PlaylistCell"];
        
        NSEntityDescription *entityDesc = [NSEntityDescription entityForName:@"Playlist" inManagedObjectContext:_context];
        
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        
        [request setEntity:entityDesc];
        
        NSError *error;
        
        cell.detailTextLabel.text = [NSString stringWithFormat:@"%u", [_context executeFetchRequest:request error:&error].count];
        
    } else if (indexPath.row == 2) {
        cell = [tableView dequeueReusableCellWithIdentifier:@"FavoritosCell"];
    }
    
    return cell;
}

@end
